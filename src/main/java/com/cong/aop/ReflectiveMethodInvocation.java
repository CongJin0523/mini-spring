package com.cong.aop;

import com.cong.aop.adapter.MethodBeforeAdviceInterceptor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * uss reflect to implement method invocation
 */
public class ReflectiveMethodInvocation implements MethodInvocation {
  private final Object target;
  private final Method method;
  private final Object[] arguments;
  protected final List<MethodInterceptor> interceptors;
  protected int currentInterceptorIndex = -1;

  public ReflectiveMethodInvocation(Object target, Method method, Object[] arguments, List<MethodInterceptor> interceptors) {
    this.target = target;
    this.method = method;
    this.arguments = arguments;
    this.interceptors = interceptors;
  }

  @Override
  public Method getMethod() {
    return method;
  }

  @Override
  public Object getThis() {
    return target;
  }

  @Override
  public Object[] getArguments() {
    return arguments;
  }

  @Override
  public Object proceed() throws Throwable {
    // 如果所有拦截器都已经调用完，则调用目标方法
    if (currentInterceptorIndex >= interceptors.size() - 1) {
      return method.invoke(target, arguments);
    }

    // 获取下一个拦截器
    MethodInterceptor interceptor = interceptors.get(++currentInterceptorIndex);

    try {
      // 调用拦截器
      return interceptor.invoke(this);
    } catch (Throwable ex) {
      // 如果发生异常，确保所有前置通知都已执行
      if (interceptor instanceof MethodBeforeAdviceInterceptor) {
        currentInterceptorIndex++;
        if (currentInterceptorIndex < interceptors.size()
          && interceptors.get(currentInterceptorIndex) instanceof MethodBeforeAdviceInterceptor) {
          return proceed();
        }
      }
      throw ex;
    }
  }
}
