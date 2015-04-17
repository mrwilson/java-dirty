package uk.co.probablyfine.dirty.utils;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.function.BiFunction;

public enum Types {

  BYTE(Byte.TYPE,       1, (buffer, index, object) -> buffer.put(index, (byte) object),         ByteBuffer::get),
  SHORT(Short.TYPE,     2, (buffer, index, object) -> buffer.putShort(index, (short) object),   ByteBuffer::getShort),
  INT(Integer.TYPE,     4, (buffer, index, object) -> buffer.putInt(index, (int) object),       ByteBuffer::getInt),
  LONG(Long.TYPE,       8, (buffer, index, object) -> buffer.putLong(index, (long) object),     ByteBuffer::getLong),
  FLOAT(Float.TYPE,     4, (buffer, index, object) -> buffer.putFloat(index, (float) object),   ByteBuffer::getFloat),
  DOUBLE(Double.TYPE,   8, (buffer, index, object) -> buffer.putDouble(index, (double) object), ByteBuffer::getDouble),
  CHAR(Character.TYPE,  2, (buffer, index, object) -> buffer.putChar(index, (char) object),     ByteBuffer::getChar),
  BOOLEAN(Boolean.TYPE, 2, (buffer, index, object) -> buffer.put(index, fromBoolean((boolean) object)), (buffer, index) -> toBoolean(buffer.get(index)));

  private final Class<?> type;
  private final int size;
  private TriConsumer<ByteBuffer, Integer, Object> writeField;
  private BiFunction<ByteBuffer, Integer, Object> readField;

  Types(Class<?> type, int size, TriConsumer<ByteBuffer, Integer, Object> writeField, BiFunction<ByteBuffer, Integer, Object> readField) {
    this.type = type;
    this.size = size;
    this.writeField = writeField;
    this.readField = readField;
  }

  public static Types of(Class<?> type) {
    String name = type.getName().toUpperCase();
    return Types.valueOf(name);
  }

  public static byte fromBoolean(boolean b) {
    return (byte) (b ? 0 : 1);
  }

  public static boolean toBoolean(byte s) {
    return s == 0;
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

  public TriConsumer<ByteBuffer, Integer, Object> getWriteField() {
    return writeField;
  }

  public BiFunction<ByteBuffer, Integer, Object> getReadField() {
    return readField;
  }

  public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
  }
}
