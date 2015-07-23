package uk.co.probablyfine.dirty;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.util.stream.IntStream.range;

@State(Scope.Benchmark)
public class BenchmarkMain {

  private Store<SampleObject> store;

  @Setup
  public void setup() throws IOException {
    File file = File.createTempFile("java-dirty-", ".db");
    file.deleteOnExit();
    this.store = Store.of(SampleObject.class).from(file.getCanonicalPath());

    range(0, 2_000_000)
        .mapToObj(SampleObject::new)
        .forEach(store::put);
  }

  @Benchmark
  public void readFromStore(Blackhole blackhole) {
    store.all().forEach(blackhole::consume);
  }

  public static void main(String... args) throws RunnerException {
    Options options = new OptionsBuilder()
        .warmupIterations(6)
        .forks(1)
        .measurementIterations(10)
        .threads(10)
        .mode(Mode.All)
        .timeUnit(TimeUnit.MICROSECONDS)
        .build();

    new Runner(options).run();
  }
}
