package uk.co.probablyfine.dirty.model;

import java.util.Random;

public class SimpleObject {

    public int field;

    public SimpleObject() {
    }

    public SimpleObject(int field) {
        this.field = field;
    }

    public static SimpleObject from(Random random) {
        return new SimpleObject(random.nextInt());
    }
}
