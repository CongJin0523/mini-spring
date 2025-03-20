package com.cong.aop.adapter;

import com.cong.aop.Advice;
import com.cong.aop.MethodInterceptor;

public interface AdvisorAdapter {
  /**
   * 判断是否支持给定的通知
   *
   * @param advice 通知
   * @return 是否支持
   */
  boolean supportsAdvice(Advice advice);

  /**
   * 将通知转换为方法拦截器
   *
   * @param advice 通知
   * @return 方法拦截器
   */
  MethodInterceptor getInterceptor(Advice advice);
}

