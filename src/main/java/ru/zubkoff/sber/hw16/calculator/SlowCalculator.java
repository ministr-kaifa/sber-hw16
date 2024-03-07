package ru.zubkoff.sber.hw16.calculator;

import java.time.Duration;
import java.util.List;

import ru.zubkoff.sber.hw16.cache.Cacheable;
import ru.zubkoff.sber.hw16.cache.providers.jdbc.postgres.PostgresJdbcCache;

public class SlowCalculator extends Calculator {

  private final Duration sleepDuration;

  public SlowCalculator(Duration sleepDuration) {
    this.sleepDuration = sleepDuration;
  }

  public SlowCalculator() {
    this(Duration.ZERO);
  }
  
  @Override
  @Cacheable(PostgresJdbcCache.class) 
  public List<Integer> fibonacci(int length) {
    try {
      Thread.sleep(sleepDuration.toMillis());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return super.fibonacci(length);
  }
}