package uk.co.probablyfine.dirty.benchmarks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import uk.co.probablyfine.dirty.Store;
import uk.co.probablyfine.dirty.model.ComplexObject;

import java.io.IOException;
import java.util.Random;

import static java.util.stream.IntStream.range;
import static uk.co.probablyfine.dirty.util.StoreFactory.createStore;

@State(Scope.Benchmark)
public class ComplexObject_SingleReader {

    private Store<ComplexObject> store;
    private Random random;

    @Setup
    public void setup() throws IOException {
        this.store = createStore(ComplexObject.class);
        this.random = new Random();

        range(0, 2_000_000)
            .mapToObj(i -> ComplexObject.from(random))
            .forEach(store::put);
    }

    @Benchmark
    @Threads(1)
    public void read2mObjectsFromStore(Blackhole blackhole) {
        store.all().forEach(blackhole::consume);
    }

}