package com.cong.aop;

import java.util.ArrayList;
import java.util.List;

/**
 * aop config manage
 * including target object, interceptor
 */
public class AdvisedSupport {
  // 是否使用CGLIB代理
  private boolean proxyTargetClass = false;

  // 目标对象
  private TargetSource targetSource;

  // 方法拦截器
  private final List<MethodInterceptor> methodInterceptors = new ArrayList<>();

  // 方法匹配器(检查目标方法是否符合通知条件)
  private MethodMatcher methodMatcher;

  public boolean isProxyTargetClass() {
    return proxyTargetClass;
  }

  public void setProxyTargetClass(boolean proxyTargetClass) {
    this.proxyTargetClass = proxyTargetClass;
  }

  public TargetSource getTargetSource() {
    return targetSource;
  }

  public void setTargetSource(TargetSource targetSource) {
    this.targetSource = targetSource;
  }

  public List<MethodInterceptor> getMethodInterceptors() {
    return methodInterceptors;
  }

  public void addMethodInterceptor(MethodInterceptor methodInterceptor) {
    this.methodInterceptors.add(methodInterceptor);
  }

  public MethodMatcher getMethodMatcher() {
    return methodMatcher;
  }

  public void setMethodMatcher(MethodMatcher methodMatcher) {
    this.methodMatcher = methodMatcher;
  }
}
