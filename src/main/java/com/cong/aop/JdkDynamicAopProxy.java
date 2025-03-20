package com.cong.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * basic jdk dynamic proxy to implement aop
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
  private final AdvisedSupport advised;
  public JdkDynamicAopProxy(AdvisedSupport advised) {
    this.advised = advised;
  }
  @Override
  public Object getProxy() {
    return getProxy(getClass().getClassLoader());
  }
  @Override
  public Object getProxy(ClassLoader classLoader) {
    Class<?> targetClass = advised.getTargetSource().getTargetClass();
    if (targetClass == null) {
      throw new IllegalStateException("Target class is null");
    }
    return Proxy.newProxyInstance(classLoader, targetClass.getInterfaces(), this);
  }
  @Override
  public Object invoke(Object porxy, Method method, Object[] args) throws Throwable {
    Object target = advised.getTargetSource().getTarget();
    //have methodMatcher, only method in matcher, other all methods in class
    if (advised.getMethodMatcher() != null
      && !advised.getMethodMatcher().matches(method, target.getClass())) {
      return method.invoke(target, args);
    }
    List<MethodInterceptor> interceptors = advised.getMethodInterceptors();

    MethodInvocation invocation = new ReflectiveMethodInvocation(target, method, args, interceptors);

    return invocation.proceed();
  }
}
