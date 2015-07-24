package uk.co.probablyfine.dirty.model;

import java.util.Random;

public class ComplexObject {

    public int a;
    public long b;
    public float c;
    public double d;
    public short e;
    public byte f;
    public char g;
    public boolean h;

    public ComplexObject(int a, long b, float c, double d, short e, byte f, char g, boolean h) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.g = g;
        this.h = h;
    }

    public ComplexObject(){}

    public static ComplexObject from(Random random) {
        return new ComplexObject(
            random.nextInt(),
            random.nextLong(),
            random.nextFloat(),
            random.nextDouble(),
            (short) random.nextInt(Short.MAX_VALUE),
            (byte) random.nextInt(Byte.MAX_VALUE),
            (char) random.nextInt(Character.MAX_VALUE),
            random.nextBoolean()
        );
    }
}
