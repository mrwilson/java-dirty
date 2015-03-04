package uk.co.probablyfine.dirty;

import org.junit.Test;
import uk.co.probablyfine.dirty.testobjects.SmallObject;
import uk.co.probablyfine.dirty.testobjects.HasEveryPrimitiveField;

import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DirtyDBTest {

  @Test
  public void shouldPersistObjectWithDifferentTypedFields() throws Exception {
    File tempFile = File.createTempFile("dirty", "db");
    tempFile.deleteOnExit();

    DirtyDB<HasEveryPrimitiveField> testObjectStore = DirtyDB.of(HasEveryPrimitiveField.class).from(tempFile.getPath());

    HasEveryPrimitiveField testObject = new HasEveryPrimitiveField(1, 2L, 3.0f, 4.0d, (short) 5,(byte) 6, 'g', true);

    testObjectStore.put(testObject);

    List<HasEveryPrimitiveField> collect = testObjectStore.all().collect(toList());

    assertThat(collect, hasItems(testObject));
  }

  @Test
  public void shouldNotSyncToDiskOnEachWrite() throws Exception {
    File tempFile = File.createTempFile("fast", "db");
    tempFile.deleteOnExit();

    DirtyDB<SmallObject> testObjectStore = DirtyDB.of(SmallObject.class).from(tempFile.getPath());

    long elapsedTime = timeElapsed(() ->
        range(0, 100)
        .mapToObj(SmallObject::new)
        .forEach(testObjectStore::put)
    );

    assertTrue("Should have taken < 200ms, took "+elapsedTime, elapsedTime < 200);

  }

  @Test
  public void shouldBeAbleToOpenAndReadExistingStoreFile() throws Exception {
    File tempFile = File.createTempFile("flushed", "db");
    tempFile.deleteOnExit();

    DirtyDB<HasEveryPrimitiveField> testObjectStore = DirtyDB.of(HasEveryPrimitiveField.class).from(tempFile.getPath());
    HasEveryPrimitiveField hasEveryPrimitiveField = new HasEveryPrimitiveField(1, 2L, 3.0f, 4.0d, (short) 5,(byte) 6, 'g', true);
    testObjectStore.put(hasEveryPrimitiveField);

    // Load up a new instance from the same file

    testObjectStore = DirtyDB.of(HasEveryPrimitiveField.class).from(tempFile.getPath());
    List<HasEveryPrimitiveField> collect = testObjectStore.all().collect(toList());

    assertThat(collect.get(0), is(hasEveryPrimitiveField));
  }

  private long timeElapsed(Runnable r) {
    long time = System.currentTimeMillis();
      r.run();
    return System.currentTimeMillis() - time;
  }

}