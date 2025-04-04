package com.cong.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

public class CglibAopProxy implements AopProxy {

  private final AdvisedSupport advised;

  /**
   * 构造函数
   *
   * @param advised AOP配置
   */
  public CglibAopProxy(AdvisedSupport advised) {
    this.advised = advised;
  }

  @Override
  public Object getProxy() {
    return getProxy(null);
  }

  @Override
  public Object getProxy(ClassLoader classLoader) {
    Class<?> rootClass = advised.getTargetSource().getTargetClass();
    if (rootClass == null) {
      throw new IllegalStateException("target class is null");
    }

    Enhancer enhancer = new Enhancer();
    if (classLoader != null) {
      enhancer.setClassLoader(classLoader);
    }
    enhancer.setSuperclass(rootClass);
    enhancer.setCallback(new DynamicAdvisedInterceptor(advised));

    return enhancer.create();
  }

  /**
   * CGLIB方法拦截器
   * 实现方法的拦截和增强
   */
  private static class DynamicAdvisedInterceptor implements MethodInterceptor {

    private final AdvisedSupport advised;

    public DynamicAdvisedInterceptor(AdvisedSupport advised) {
      this.advised = advised;
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
      Object target = advised.getTargetSource().getTarget();
      // 检查方法是否匹配切点表达式
      if (advised.getMethodMatcher() != null
        && !advised.getMethodMatcher().matches(method, target.getClass())) {
        return methodProxy.invoke(target, args);
      }

      // 创建拦截器链
      List<com.cong.aop.MethodInterceptor> interceptors = advised.getMethodInterceptors();

      // 创建方法调用
      CglibMethodInvocation invocation = new CglibMethodInvocation(target, method, args, methodProxy, interceptors);

      // 执行方法调用链
      return invocation.proceed();
    }
  }
}