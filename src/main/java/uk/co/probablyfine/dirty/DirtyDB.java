package uk.co.probablyfine.dirty;

import uk.co.probablyfine.dirty.utils.Classes;
import uk.co.probablyfine.dirty.utils.Nio;
import uk.co.probablyfine.dirty.utils.Types;

import java.lang.reflect.Field;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.co.probablyfine.dirty.utils.Exceptions.unchecked;

public class DirtyDB<T> {

  private final FileChannel fileChannel;
  private final MappedByteBuffer memoryMappedFile;
  private final int offSet;
  private final List<Field> fields;
  private final Class<T> klass;
  private int size;

  public DirtyDB(String path, Class<T> klass) {
    this.klass = klass;
    this.fields = Classes.primitiveFields(klass).collect(Collectors.toList());
    this.fileChannel = Nio.fileChannel(path);
    this.offSet = Types.offSetForClass(klass);
    this.memoryMappedFile = Nio.mapFile(fileChannel, 10*offSet);
    this.size = 0;
  }

  public void put(T t) {
    fields.forEach(field -> {
      Object unchecked = unchecked(() -> field.get(t));
      memoryMappedFile.putInt((int) unchecked);
    });

    this.size++;
  }

  public Stream<T> all() {
    Stream.Builder<T> builder = Stream.builder();

    for (int i = 0; i < this.size; i++) {
      AtomicInteger cursor = new AtomicInteger(i * this.offSet);
      T t = unchecked(klass::newInstance);

      fields.forEach(field -> {
        unchecked(() -> field.set(t, memoryMappedFile.getInt(cursor.get())));
        cursor.addAndGet(Types.INT.getSize());
      });

      builder.add(t);
    }

    return builder.build();
  }

  public interface WithFile {
    DirtyDB from(String path);
  }

  public static WithFile of(final Class<?> fooClass) {
    return path -> new DirtyDB(path, fooClass);
  }
}
