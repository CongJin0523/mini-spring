package com.cong.aop;

import java.lang.reflect.Method;

/**
 * check if method is able to aspect
 */
public interface MethodMatcher {
  boolean matches(Method method, Class<?> targetClass);
}
