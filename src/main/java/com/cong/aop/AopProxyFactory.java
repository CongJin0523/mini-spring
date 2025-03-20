package com.cong.aop;

public interface AopProxyFactory {

  /**
   * creaing AopProxy
   *
   * @param config AOP配置
   * @return AOP代理
   */
  AopProxy createAopProxy(AdvisedSupport config);
}
