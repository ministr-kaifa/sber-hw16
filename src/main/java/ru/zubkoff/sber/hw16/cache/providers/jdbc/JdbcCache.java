package ru.zubkoff.sber.hw16.cache.providers.jdbc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import ru.zubkoff.sber.hw16.cache.providers.Cache;
import ru.zubkoff.sber.hw16.serialization.utils.SerializationUtils;

public abstract class JdbcCache implements Cache {

  private final Connection connection;
  
  protected Connection getConnection() {
    return connection;
  }

  protected JdbcCache(Connection connection) throws SQLException {
    this.connection = connection;
    tryInit();
  }

  protected abstract void addCachedResult(String methodId, byte[] serializedArgs, byte[] serializedResult) throws SQLException;

  protected abstract Optional<Object> findCachedResult(String methodId, byte[] serializedArgs) throws SQLException;

  protected abstract void tryInit() throws SQLException;

  private static byte[] serializeArgs(List<Object> args) {
    try {
      return SerializationUtils.serialize(args);
    }
    catch (IOException e) {
      throw new RuntimeException("Exception occured on args serrialization", e);
    }
  } 

  private static byte[] serializeResult(Object result) {
    try {
      return SerializationUtils.serialize(result);
    }
    catch (IOException e) {
      throw new RuntimeException("Exception occured on result serrialization", e);
    }
  }

  @Override
  public Object evaluateIfAbsent(Method method, List<Object> args, Supplier<Object> resultEvaluation) {
    var methodId = methodId(method);
    var serializedArgs = serializeArgs(args);
    Optional<Object> cachedResult;
    try {
      cachedResult = findCachedResult(methodId, serializedArgs);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    if(cachedResult.isPresent()) {
      return cachedResult.get();
    } else {
      var result = resultEvaluation.get();
      try {
        addCachedResult(methodId, serializedArgs, serializeResult(result));
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
      return result;
    }
  }

  private static String methodId(Method method) {
    return method.getDeclaringClass().getCanonicalName() + "#" + method.getName();
  }

}
