package com.cong.aop;

import java.lang.reflect.Method;

/**
 * get method info
 */
public interface MethodInvocation {
  Method getMethod();
  Object getThis();
  Object[] getArguments();
  Object proceed() throws Throwable;
}
