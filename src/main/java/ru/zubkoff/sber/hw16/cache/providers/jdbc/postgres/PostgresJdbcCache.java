package ru.zubkoff.sber.hw16.cache.providers.jdbc.postgres;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import ru.zubkoff.sber.hw16.cache.providers.jdbc.JdbcCache;
import ru.zubkoff.sber.hw16.serialization.utils.SerializationUtils;

public class PostgresJdbcCache extends JdbcCache {
  private static final String FIND_CACHE_SQL = """
    SELECT
      serialized_result
    FROM cache
    WHERE 
      method_id = ? AND
      serialized_args = ?;
  """;

  private static final String ADD_CACHE_SQL = """
    INSERT INTO
      cache
      (method_id, serialized_args, serialized_result)
    VALUES
      (?, ?, ?);
  """;

  private static final String TRY_CREATE_CACHE_TABLE_SQL = """
    CREATE TABLE IF NOT EXISTS cache
      (method_id text, serialized_args bytea, serialized_result bytea);
  """;

  public PostgresJdbcCache(Connection connection) throws SQLException {
    super(connection);
  }

  @Override
  protected void addCachedResult(String methodId, byte[] serializedArgs, byte[] serializedResult) throws SQLException {
    var connection = getConnection();
    try(var addCacheStatement = connection.prepareStatement(ADD_CACHE_SQL);) {
      addCacheStatement.setString(1, methodId);
      try(var serializedArgsAsStream = new ByteArrayInputStream(serializedArgs);) {
        addCacheStatement.setBinaryStream(2, serializedArgsAsStream);
      } catch (IOException e) {
        throw new RuntimeException("IO exception occured while closing serializedArgsAsStream", e);
      }
      try(var serializedResultAsStream = new ByteArrayInputStream(serializedResult);) {
        addCacheStatement.setBinaryStream(3, serializedResultAsStream);
      } catch (IOException e) {
        throw new RuntimeException("IO exception occured while closing serializedResultAsStream", e);
      }
      var rowsChanged = addCacheStatement.executeUpdate();
      if(rowsChanged <= 0) {
        throw new RuntimeException("Unsuccessful cached result insertion");
      }
    }
  }

  @Override
  protected Optional<Object> findCachedResult(String methodId, byte[] serializedArgs) throws SQLException {
    var connection = getConnection();
    try(var findCacheStatement = connection.prepareStatement(FIND_CACHE_SQL)) {
      findCacheStatement.setString(1, methodId);
      try(var serializedArgsAsStream = new ByteArrayInputStream(serializedArgs);) {
        findCacheStatement.setBinaryStream(2, serializedArgsAsStream);
      } catch (IOException e) {
        throw new RuntimeException("IO exception occured while closing serializedArgsAsStream", e);
      }
      var foundCache = findCacheStatement.executeQuery();
      if(foundCache.next()) { 
        var serializedResult = foundCache.getBytes("serialized_result");
        try {
          return Optional.of(SerializationUtils.deserialize(serializedResult));
        } catch (ClassNotFoundException | IOException e) {
          throw new RuntimeException("Unsuccessful found result deserialization", e);
        }
      } else {
        return Optional.empty();
      }
    }
  }

  @Override
  protected void tryInit() throws SQLException {
    var connection = getConnection();
    try(var createCacheTableStatement = connection.createStatement();) {
      createCacheTableStatement.executeUpdate(TRY_CREATE_CACHE_TABLE_SQL);
    }
  }

}
