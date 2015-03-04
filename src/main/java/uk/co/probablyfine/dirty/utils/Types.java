package uk.co.probablyfine.dirty.utils;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public enum Types {

  BYTE(Byte.TYPE,      1, (buffer, object) -> buffer.put((byte) object),         ByteBuffer::get),
  SHORT(Short.TYPE,    2, (buffer, object) -> buffer.putShort((short) object),   ByteBuffer::getShort),
  INT(Integer.TYPE,    4, (buffer, object) -> buffer.putInt((int) object),       ByteBuffer::getInt),
  LONG(Long.TYPE,      8, (buffer, object) -> buffer.putLong((long) object),     ByteBuffer::getLong),
  FLOAT(Float.TYPE,    4, (buffer, object) -> buffer.putFloat((float) object),   ByteBuffer::getFloat),
  DOUBLE(Double.TYPE,  8, (buffer, object) -> buffer.putDouble((double) object), ByteBuffer::getDouble),
  CHAR(Character.TYPE, 2, (buffer, object) -> buffer.putChar((char) object),     ByteBuffer::getChar);

  private final Class<?> type;
  private final int size;
  private BiConsumer<ByteBuffer, Object> writeField;
  private BiFunction<ByteBuffer, Integer, Object> readField;

  Types(Class<?> type, int size, BiConsumer<ByteBuffer, Object> writeField, BiFunction<ByteBuffer, Integer, Object> readField) {
    this.type = type;
    this.size = size;
    this.writeField = writeField;
    this.readField = readField;
  }

  public static Types of(Class<?> type) {
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

  public BiConsumer<ByteBuffer, Object> getWriteField() {
    return writeField;
  }

  public BiFunction<ByteBuffer, Integer, Object> getReadField() {
    return readField;
  }
}
