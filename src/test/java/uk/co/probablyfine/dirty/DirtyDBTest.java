package uk.co.probablyfine.dirty;

import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DirtyDBTest {

  static class Foo {
    int a, b, c;

    public Foo(int a, int b, int c) {
      this.a = a;
      this.b = b;
      this.c = c;
    }

    public Foo() {}

    @Override
    public String toString() {
      return "Foo{" +
          "a=" + a +
          ", b=" + b +
          ", c=" + c +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Foo foo = (Foo) o;

      if (a != foo.a) return false;
      if (b != foo.b) return false;
      if (c != foo.c) return false;

      return true;
    }

  }

  @Test
  public void shouldPersistMonoTypedObject() throws Exception {
    File tempFile = File.createTempFile("dirty", "db");
    tempFile.deleteOnExit();

    DirtyDB<Foo> fooStore = DirtyDB.of(Foo.class).from(tempFile.getPath());

    Foo foo = new Foo(1, 2, 3);

    fooStore.put(foo);

    List<Foo> collect = fooStore.all().collect(toList());

    assertThat(collect, hasItems(foo));
  }

}