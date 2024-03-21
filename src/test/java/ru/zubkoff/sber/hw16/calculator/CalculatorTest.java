package ru.zubkoff.sber.hw16.calculator;

import static org.junit.Assert.assertArrayEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class CalculatorTest {

  @Test
  void givenExpectedOutputs_whenCalculating_thenCalculatingResultsEqualsToExpected() {
    var expect = List.of(
      0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,
      1597,2584,4181,6765,10946,17711,28657,46368,75025,
      121393,196418,317811,514229,832040,1346269,
      2178309,3524578,5702887,9227465,14930352,24157817,
      39088169,63245986,102334155);

    var calculator = new Calculator();

    for (int i = 0; i < expect.size(); i++) {
      assertArrayEquals(
        calculator.fibonacci(i).toArray(new Integer[0]),
        expect.subList(0, i).toArray(new Integer[0])
      );
    }
    
  }
}
