package com.cong.aop.adapter;

import com.cong.aop.MethodBeforeAdvice;
import com.cong.aop.MethodInterceptor;
import com.cong.aop.MethodInvocation;

public class MethodBeforeAdviceInterceptor implements MethodInterceptor {
  private final MethodBeforeAdvice advice;

  /**
   * 构造函数
   *
   * @param advice 方法前置通知
   */
  public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
    this.advice = advice;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    // 执行前置通知
    advice.before(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
    // 执行目标方法
    return invocation.proceed();
  }
}
