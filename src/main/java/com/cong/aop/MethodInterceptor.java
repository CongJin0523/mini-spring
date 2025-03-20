package com.cong.aop;

/**
 * method interceptor
 * use for advice
 */
public interface MethodInterceptor{
  Object invoke(MethodInvocation invocation) throws Throwable;
}
