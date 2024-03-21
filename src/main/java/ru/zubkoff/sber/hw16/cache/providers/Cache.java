package ru.zubkoff.sber.hw16.cache.providers;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;

public interface Cache {
  Object evaluateIfAbsent(Method method, List<Object> args, Supplier<Object> resultEvaluation);
}
