### Школа Java Developer
#### Домашнее задание №16

```java
public interface Cache {
  Object evaluateIfAbsent(Method method, List<Object> args, Supplier<Object> resultEvaluation);
}
```
Интерфейс кеша с одним методом, работающим по принципу если есть закешированный вызов метода method с аргументами args возвращаем значение из кеша, иначе вызываем resultEvaluation который вернет нам результат, этот результат мы кешируем и возвращаем из метода, для примера я оставил HashMapCache все кеши должны работать по такому принципу

```java
public class Calculator {
  ...
}

public class SlowCalculator extends Calculator {
  ...
}
```
Calculator имеет только метод fibonacci(int length) и он возвращает ряд фибоначи размером length,  SlowCalculator делает то же самое но в конструкторе мы можем передать ему время замедления выполнения, перед тем как вернуть ряд он будет вызывать sleep на заданное в конструкторе время

```java
public class SerializationUtils {
  ...
}
```
Содержит статические методы сериализации/десериализации в разные форматы из разных данных

```java
public class CacheResolver {
  ...
}
```
Просто коллекция кэшей, нужен сугубо как аргумент CacheWrapperFactory

```java
public class CacheWrapperFactory {
  ...
}
```
Содержит только метод T wrap(T obj) и конструктор принимающий CacheResolver. Метод wrap создает обертку CachedInvocationHandler которому передает кеш резолвер

```java
public class CachedInvocationHandler implements MethodInterceptor {
  ...
}
```
Кеширующая обертка, колбек будет вызываться при любом вызываемом методе обернутого объекта, в колбеке мы проверяем содержит ли метод @Cacheable если нет просто вызываем метод и возвращаем значение, если аннотация представлена, вызываем cacheResolver.resolve(cacheable) который должен нам вернуть объект Cache у которого мы вызовем evaluateIfAbsent

```java
public abstract class JdbcCache implements Cache {
  ...
}
```
JdbcCache представляет набор общих не специфичных методов и реализацию evaluateIfAbsent, специфичные же методы остаются на реализацию дргуим классам которые JdbcCache будет вызывать: 
```java
protected abstract void addCachedResult(String methodId, byte[] serializedArgs, byte[] serializedResult) throws SQLException;

protected abstract Optional<Object> findCachedResult(String methodId, byte[] serializedArgs) throws SQLException;

protected abstract void tryInit() throws SQLException;
```

```java
public class PostgresJdbcCache extends JdbcCache {
  ...
}
```
Реализация JdbcCache спецефичная для постгрес
