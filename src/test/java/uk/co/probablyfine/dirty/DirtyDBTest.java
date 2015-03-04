package uk.co.probablyfine.dirty;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class DirtyDBTest {

  @Test
  public void shouldPersistObjectWithDifferentTypedFields() throws Exception {
    File tempFile = File.createTempFile("dirty", "db");
    tempFile.deleteOnExit();

    DirtyDB<TestObject> testObjectStore = DirtyDB.of(TestObject.class).from(tempFile.getPath());

    TestObject testObject = new TestObject(1, 2L, 3.0f, 4.0d, (short) 5,(byte) 6, 'g');

    testObjectStore.put(testObject);

    List<TestObject> collect = testObjectStore.all().collect(toList());

    assertThat(collect, hasItems(testObject));
  }

}