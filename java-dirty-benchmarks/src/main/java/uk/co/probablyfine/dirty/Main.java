package uk.co.probablyfine.dirty;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import uk.co.probablyfine.dirty.benchmarks.SimpleObject_SingleReader;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Main {

    public static void main(String... args) throws RunnerException {
        ChainedOptionsBuilder builder = new OptionsBuilder()
            .warmupIterations(20)
            .forks(1)
            .measurementIterations(50)
            .mode(Mode.All)
            .timeUnit(TimeUnit.MILLISECONDS);

        Stream.of(SimpleObject_SingleReader.class)
                .map(Class::getName)
                .forEach(builder::include);

        new Runner(builder.build()).run();
    }
}
