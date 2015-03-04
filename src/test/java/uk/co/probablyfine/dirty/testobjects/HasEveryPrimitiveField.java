package uk.co.probablyfine.dirty.testobjects;

public class HasEveryPrimitiveField {
  public int a;
  public long b;
  public float c;
  public double d;
  public short e;
  public byte f;
  public char g;
  public boolean h;

  public HasEveryPrimitiveField(int a, long b, float c, double d, short e, byte f, char g, boolean h) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    this.e = e;
    this.f = f;
    this.g = g;
    this.h = h;
  }

  public HasEveryPrimitiveField(){}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    HasEveryPrimitiveField that = (HasEveryPrimitiveField) o;

    if (a != that.a) return false;
    if (b != that.b) return false;
    if (Float.compare(that.c, c) != 0) return false;
    if (Double.compare(that.d, d) != 0) return false;
    if (e != that.e) return false;
    if (f != that.f) return false;
    if (g != that.g) return false;
    if (h != that.h) return false;

    return true;
  }

  @Override
  public String toString() {
    return "TestObject{" +
        "a=" + a +
        ", b=" + b +
        ", c=" + c +
        ", d=" + d +
        ", e=" + e +
        ", f=" + f +
        ", g=" + g +
        ", h=" + h +
        '}';
  }
}
