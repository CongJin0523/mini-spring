package com.cong.aop.adapter;

import com.cong.aop.Advice;
import com.cong.aop.AfterReturningAdvice;
import com.cong.aop.MethodInterceptor;

public class AfterReturningAdviceAdapter implements AdvisorAdapter{
  @Override
  public boolean supportsAdvice(Advice advice) {
    return advice instanceof AfterReturningAdvice;
  }

  @Override
  public MethodInterceptor getInterceptor(Advice advice) {
    return new AfterReturningAdviceInterceptor((AfterReturningAdvice) advice);
  }
}
