package uk.co.probablyfine.dirty.utils;

import java.lang.reflect.Field;

public enum Types {

  INT(Integer.TYPE, 4);

  private final Class<?> type;
  private final int size;

  Types(Class<?> type, int size) {
    this.type = type;
    this.size = size;
  }

  static Types of(Class<?> type) {
    String name = type.getName().toUpperCase();
    return Types.valueOf(name);
  }

  public static <T> int offSetForClass(Class<T> fooClass) {
    return Classes.primitiveFields(fooClass)
        .map(Field::getType)
        .map(Types::of)
        .mapToInt(Types::getSize)
        .sum();
  }

  public int getSize() {
    return size;
  }
}
