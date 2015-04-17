package uk.co.probablyfine.dirty.utils;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class Classes {

  private Classes() {
    //Utility classes have no public constructor
  }

  public static Stream<Field> primitiveFields(Class<?> klass) {
    return asList(klass.getDeclaredFields())
        .stream()
        .filter(x -> x.getType().isPrimitive());
  }
}
