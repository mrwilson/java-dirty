package uk.co.probablyfine.dirty;

public class TestObject {
  int a;
  long b;
  float c;
  double d;
  short e;
  byte f;
  char g;

  public TestObject(int a, long b, float c, double d, short e, byte f, char g) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    this.e = e;
    this.f = f;
    this.g = g;
  }

  public TestObject(){}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TestObject testObject = (TestObject) o;

    if (a != testObject.a) return false;
    if (b != testObject.b) return false;
    if (Float.compare(testObject.c, c) != 0) return false;
    if (Double.compare(testObject.d, d) != 0) return false;
    if (e != testObject.e) return false;
    if (f != testObject.f) return false;
    if (g != testObject.g) return false;

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
        '}';
  }
}
