package com.cong.aop.adapter;

import com.cong.aop.Advice;
import com.cong.aop.MethodInterceptor;

public interface AdvisorAdapterRegistry {
  /**
   * 注册通知适配器
   *
   * @param adapter 通知适配器
   */
  void registerAdvisorAdapter(AdvisorAdapter adapter);

  /**
   * 将通知转换为方法拦截器
   *
   * @param advice 通知
   * @return 方法拦截器
   */
  MethodInterceptor[] getInterceptors(Advice advice);

  /**
   * 包装通知为方法拦截器
   *
   * @param advice 通知
   * @return 方法拦截器
   */
  MethodInterceptor wrap(Advice advice);
}
