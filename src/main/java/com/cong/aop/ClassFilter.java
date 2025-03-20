package com.cong.aop;

public interface ClassFilter {
  boolean matches(Class<?> targetClass);
}
