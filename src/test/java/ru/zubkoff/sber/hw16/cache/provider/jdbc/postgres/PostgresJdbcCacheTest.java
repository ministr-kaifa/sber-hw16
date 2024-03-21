package ru.zubkoff.sber.hw16.cache.provider.jdbc.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import ru.zubkoff.sber.hw16.cache.CacheResolver;
import ru.zubkoff.sber.hw16.cache.CacheWrapperFactory;
import ru.zubkoff.sber.hw16.cache.providers.jdbc.postgres.PostgresJdbcCache;
import ru.zubkoff.sber.hw16.calculator.SlowCalculator;

class PostgresJdbcCacheTest {

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:15-alpine"
  );

  static PostgresJdbcCache cache;
  static CacheResolver cacheResolver;
  static CacheWrapperFactory cacheFactory;

  @BeforeAll
  static void beforeAll() throws SQLException {
    postgres.start();
    var connection = DriverManager.getConnection(
      postgres.getJdbcUrl(),
      postgres.getUsername(), 
      postgres.getPassword()
    );
    cache = new PostgresJdbcCache(connection);
    cacheResolver = new CacheResolver(cache);
    cacheFactory = new CacheWrapperFactory(cacheResolver);
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @Test
  void givenCachedSlowObject_whenExecutingItTwice_thenSecondExecutionIsFaster() {
    //given
    var input = 5;
    var slowCalculatorSleepDuration = Duration.ofSeconds(2);
    var slowCalculator = new SlowCalculator(slowCalculatorSleepDuration);
    var cachedSlowCalculator = cacheFactory.wrap(slowCalculator);

    //when then
    cachedSlowCalculator.fibonacci(input);
    assertTimeout(slowCalculatorSleepDuration, () -> cachedSlowCalculator.fibonacci(input));
  }

  @Test
  void givenCachedObjectAndSameNonCachedObject_whenExecuting_thenResultsAreSame() {
    //given
    var slowCalculatorSleepDuration = Duration.ZERO;
    var slowCalculator = new SlowCalculator(slowCalculatorSleepDuration);
    var cachedSlowCalculator = cacheFactory.wrap(slowCalculator);

    for (int i = 0; i < 100; i++) {
      //when
      var cachedResult = cachedSlowCalculator.fibonacci(i);
      var nonCachedResult = slowCalculator.fibonacci(i);

      //then
      assertEquals(nonCachedResult, cachedResult);
    }
  }

}
