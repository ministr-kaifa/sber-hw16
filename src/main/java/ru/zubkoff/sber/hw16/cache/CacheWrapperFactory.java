package ru.zubkoff.sber.hw16.cache;

import net.sf.cglib.proxy.Enhancer;

public class CacheWrapperFactory {
  private final CacheResolver cacheResolver;

  public CacheWrapperFactory(CacheResolver cacheResolver) {
    this.cacheResolver = cacheResolver;
  }

  /**
   * Creates new cache proxy wrapper instance
   * @param target object to be wrapped
   * @return new cache proxy wrapper instance
   */
  public <T> T wrap(T target) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(target.getClass());
    enhancer.setCallback(new CachedInvocationHandler(target, cacheResolver));
    return (T)enhancer.create();
  }
}
