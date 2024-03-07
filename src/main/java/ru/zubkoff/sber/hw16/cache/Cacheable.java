package ru.zubkoff.sber.hw16.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.zubkoff.sber.hw16.cache.providers.Cache;
import ru.zubkoff.sber.hw16.cache.providers.HashMapCache;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {

  /**
   * if PersistenceType.FILE then result value and args must be serializable
   */
  Class<? extends Cache> value() default HashMapCache.class;

}
