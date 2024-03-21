package ru.zubkoff.sber.hw16.cache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CachedInvocationHandler implements MethodInterceptor {

  private final CacheResolver cacheResolver;
  private final Object proxied;

  public CachedInvocationHandler(Object proxied, CacheResolver cacheResolver) {
    this.cacheResolver = cacheResolver;
    this.proxied = proxied;
  }

  @Override
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    var cacheInfo = method.getAnnotation(Cacheable.class);
    if(cacheInfo != null) {
      var cache = cacheResolver.resolve(cacheInfo);
      return cache.evaluateIfAbsent(method, List.of(args), () -> suppressedInvoke(method, args));
    } else {
      return method.invoke(proxied, args);
    }
  }

  private Object suppressedInvoke(Method method, Object[] args) {
    try {
      return method.invoke(proxied, args);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

}
