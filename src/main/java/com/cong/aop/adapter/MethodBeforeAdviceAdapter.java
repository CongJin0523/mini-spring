package com.cong.aop.adapter;

import com.cong.aop.Advice;
import com.cong.aop.MethodBeforeAdvice;
import com.cong.aop.MethodInterceptor;

public class MethodBeforeAdviceAdapter implements AdvisorAdapter {

  @Override
  public boolean supportsAdvice(Advice advice) {
    return advice instanceof MethodBeforeAdvice;
  }

  @Override
  public MethodInterceptor getInterceptor(Advice advice) {
    return new MethodBeforeAdviceInterceptor((MethodBeforeAdvice) advice);
  }
}
