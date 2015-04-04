package uk.co.probablyfine.dirty.utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static uk.co.probablyfine.dirty.utils.Exceptions.unchecked;

public class Nio {

  public static FileChannel fileChannel(String path) {
    File file = new File(path);
    return unchecked(() -> new RandomAccessFile(file, "rw")).getChannel();
  }

  public static MappedByteBuffer mapFile(FileChannel fileChannel, int initialPosition, int size) {
    return unchecked(() -> {
      return fileChannel.map(FileChannel.MapMode.READ_WRITE, initialPosition, size);
    });
  }

}
