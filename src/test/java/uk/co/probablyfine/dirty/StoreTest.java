package uk.co.probablyfine.dirty;

import org.junit.Before;
import org.junit.Test;
import uk.co.probablyfine.dirty.testobjects.HasEveryPrimitiveField;
import uk.co.probablyfine.dirty.testobjects.SmallObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

    assertThat(store.get(1).a, is(5));
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