package ru.zubkoff.sber.hw16.calculator;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Calculator {
  /**
   * Расчитывает ряд фибоначи
   * @param length длина ряда
   * @return ряд фибоначи 
   */
  public List<Integer> fibonacci(int length) {
    return Stream.generate(new Supplier<Integer>() {
      int prev = 1;
      int current = 0;
      @Override
      public Integer get() {
        var result = current;
        current += prev;
        prev = current - prev;
        return result;
      }
    }).limit(length)
      .collect(Collectors.toList());
  }
}
