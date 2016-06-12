package uk.co.probablyfine.dirty;

import com.sun.management.UnixOperatingSystemMXBean;
import org.junit.Before;
import org.junit.Test;
import uk.co.probablyfine.dirty.testobjects.HasEveryPrimitiveField;
import uk.co.probablyfine.dirty.testobjects.SmallObject;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StoreTest {

    private File storeFile;

    @Before
    public void setUp() throws Exception {
        storeFile = createTempFile();
    }

    @Test
    public void shouldPersistObjectWithDifferentTypedFields() throws Exception {
        Store<HasEveryPrimitiveField> store = Store.of(HasEveryPrimitiveField.class).from(storeFile.getPath());

        store.put(HasEveryPrimitiveField.EXAMPLE);

        List<HasEveryPrimitiveField> collect = store.all().collect(toList());

        assertThat(collect, hasItems(HasEveryPrimitiveField.EXAMPLE));
    }

    @Test
    public void shouldNotSyncToDiskOnEachWrite() throws Exception {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

        long elapsedTime = timeElapsed(() ->
                range(0, 100)
                    .mapToObj(SmallObject::new)
                    .forEach(store::put)
        );

        assertTrue("Should have taken < 200ms, took " + elapsedTime, elapsedTime < 200);
    }

    @Test
    public void shouldBeAbleToOpenAndReadExistingStoreFile() throws Exception {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

        store.put(new SmallObject(1));
        store.put(new SmallObject(2));
        store.put(new SmallObject(3));

        store = Store.of(SmallObject.class).from(storeFile.getPath());

        List<Integer> resultContents = store.all().map(x -> x.a).collect(toList());

        assertThat(resultContents.size(), is(3));
        assertThat(resultContents, hasItems(1, 2, 3));
    }

    @Test
    public void shouldSupportReturningObjects_MostRecentFirst() throws Exception {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

        store.put(new SmallObject(1));
        store.put(new SmallObject(2));
        store.put(new SmallObject(3));

        List<Integer> resultContents = store.reverse().map(x -> x.a).collect(toList());

        assertThat(resultContents, hasItems(3, 2, 1));
    }

    @Test
    public void shouldSupportDirectIndexAccess() throws Exception {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

        store.put(new SmallObject(4));
        store.put(new SmallObject(5));
        store.put(new SmallObject(6));

        Optional<SmallObject> smallObject = store.get(1);

        assertTrue(smallObject.isPresent());
        assertThat(smallObject.get().a, is(5));
    }

    @Test
    public void shouldReturnEmptyOptionalForEntryThatDoesNotExist() throws Exception {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

        Optional<SmallObject> smallObject = store.get(1);

        assertFalse(smallObject.isPresent());
    }

    @Test
    public void shouldInferCorrectSizeOfExistingStoreFile() throws Exception {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

        store.put(new SmallObject(5));
        store.put(new SmallObject(8));
        store.put(new SmallObject(1));

        store = Store.of(SmallObject.class).from(storeFile.getPath());

        List<SmallObject> resultContents = store.all().collect(Collectors.toList());

        assertThat(resultContents.size(), is(3));
    }

    @Test
    public void shouldCallWriteObserverWithObjectAndIndex() throws Exception {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());
        BiConsumer<SmallObject, Integer> observeWriteFunction = mock(BiConsumer.class);

        SmallObject first = new SmallObject(4);
        SmallObject second = new SmallObject(4);

        store.observeWrites(observeWriteFunction);

        store.put(first);
        store.put(second);

        verify(observeWriteFunction).accept(first, 0);
        verify(observeWriteFunction).accept(second, 1);
    }

    @Test
    public void shouldOverwriteOldEntriesWhenReset() throws Exception {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

        store.put(new SmallObject(5));
        store.put(new SmallObject(8));

        List<SmallObject> resultContents = store.all().collect(Collectors.toList());

        assertThat(resultContents.size(), is(2));

        store.reset();

        store.put(new SmallObject(6));

        resultContents = store.all().collect(Collectors.toList());

        assertThat(resultContents.size(), is(1));
        assertThat(resultContents.get(0).a, is(6));
    }

    @Test
    public void shouldAcceptStartingIndexForStream() throws Exception {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

        store.put(new SmallObject(2));
        store.put(new SmallObject(3));
        store.put(new SmallObject(5));
        store.put(new SmallObject(7));

        store = Store.of(SmallObject.class).from(storeFile.getPath());

        List<SmallObject> resultContents = store.from(2).collect(Collectors.toList());

        assertThat(resultContents.size(), is(2));
        assertThat(resultContents.get(0).a, is(5));
        assertThat(resultContents.get(1).a, is(7));
    }

    @Test
    public void shouldMapNewPartitionWhenEndIsReached() throws Exception {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

        List<SmallObject> collect = rangeClosed(0, 1_000_000)
            .mapToObj(SmallObject::new)
            .collect(Collectors.toList());

        collect.forEach(store::put);

        assertThat(store.all().collect(Collectors.toList()).size(), is(collect.size()));
    }

    @Test
    public void shouldReadBackMultiplePartitionsOnReload() {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

        List<SmallObject> collect = rangeClosed(0, 1_000_000)
            .mapToObj(SmallObject::new)
            .collect(Collectors.toList());

        collect.forEach(store::put);

        store = Store.of(SmallObject.class).from(storeFile.getPath());

        assertThat(store.all().collect(Collectors.toList()).size(), is(collect.size()));
    }

    @Test
    public void shouldCloseUpAllFilesWhenRequested() throws Exception {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

        long initialOpenFilesCount = openFileCount();

        store.close();

        assertThat(openFileCount(), is(initialOpenFilesCount - 1));
    }

    @Test(expected = ClosedStoreException.class)
    public void shouldThrowExceptionWhenClosed_ifTryingToWrite() {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

        store.close();

        store.put(new SmallObject(1));
    }

    @Test(expected = ClosedStoreException.class)
    public void shouldThrowExceptionWhenClosed_ifTryingToRead() {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

        store.put(new SmallObject(1));

        store.close();

        store.get(0);
    }

    @Test
    public void shouldReturnIndexOfEntryOnPut() {
        Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

        SmallObject object = new SmallObject(1);

        assertThat(store.put(object), is(0));
        assertThat(store.put(object), is(1));
        assertThat(store.put(object), is(2));
    }

    private long openFileCount() {
        return ((UnixOperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getOpenFileDescriptorCount();
    }

    private File createTempFile() throws IOException {
        File tempFile = File.createTempFile(randomUUID().toString(), ".dirty");
        tempFile.deleteOnExit();
        return tempFile;
    }

    private long timeElapsed(Runnable r) {
        long time = System.currentTimeMillis();
        r.run();
        return System.currentTimeMillis() - time;
    }

}