package uk.co.probablyfine.dirty.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.isA;

public class ExceptionsTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  @Test
  public void causeAndNestedCause() throws Exception {
    expectedException.expect(Exceptions.StoreException.class);
    expectedException.expectCause(isA(ArithmeticException.class));
    Exceptions.unchecked(()-> foo());

  }

  private void foo() {
    int a = 1/0;
  }
}