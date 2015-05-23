package uk.co.probablyfine.dirty;

import org.junit.Before;
import org.junit.Test;
import uk.co.probablyfine.dirty.testobjects.HasEveryPrimitiveField;
import uk.co.probablyfine.dirty.testobjects.SmallObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.co.probablyfine.dirty.utils.Exceptions.unchecked;

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

    assertThat(resultContents.size(),   is(2));
    assertThat(resultContents.get(0).a, is(5));
    assertThat(resultContents.get(1).a, is(7));
  }

  @Test
  public void shouldMapNewPartitionWhenEndIsReached() throws Exception {
    Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

    List<SmallObject> collect = IntStream
        .rangeClosed(0, 1_000_000)
        .mapToObj(SmallObject::new)
        .collect(Collectors.toList());

    collect.forEach(store::put);

    assertThat(store.all().collect(Collectors.toList()).size(), is(collect.size()));
  }

  @Test
  public void shouldReadBackMultiplePartitionsOnReload() {
    Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

    List<SmallObject> collect = IntStream
        .rangeClosed(0, 1_000_000)
        .mapToObj(SmallObject::new)
        .collect(Collectors.toList());

    collect.forEach(store::put);

    store = Store.of(SmallObject.class).from(storeFile.getPath());

    assertThat(store.all().collect(Collectors.toList()).size(), is(collect.size()));
  }

  @Test
  public void shouldSupportMultipleConcurrentWriters() {
    int numberOfWriters = 40;
    CyclicBarrier barrier = new CyclicBarrier(numberOfWriters + 1);
    Thread[] writeThreads = new Thread[numberOfWriters];

    Store<SmallObject> store = Store.of(SmallObject.class).from(storeFile.getPath());

    List<SmallObject> collect = IntStream
        .rangeClosed(0, 10_000)
        .mapToObj(SmallObject::new)
        .collect(Collectors.toList());

    for(int i=0; i<numberOfWriters; i++) {
      writeThreads[i] = new Thread(() -> {
        unchecked(() -> barrier.await());
        collect.forEach(store::put);
      });
      writeThreads[i].start();
    }

    unchecked(() -> barrier.await());
    unchecked(() -> Thread.sleep(200));

    assertThat(store.all().count(), is(Integer.toUnsignedLong(collect.size() * numberOfWriters)));

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