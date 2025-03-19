package com.cong.beans.factory.support;

import com.cong.beans.factory.DisposableBean;
import com.cong.beans.factory.config.BeanDefinition;

import java.lang.reflect.Method;

/**
 * adapter mode, to destroy bean togather
 */
public class DisposableBeanAdapter implements DisposableBean {
  private final Object bean;
  private final String beanName;
  private final String destroyMethodName;
  private final boolean isDisposableBean;

  public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
    this.bean = bean;
    this.beanName = beanName;
    this.destroyMethodName = beanDefinition.getDestroyMethodName();
    this.isDisposableBean = bean instanceof DisposableBean;
  }

  @Override
  public void destroy() throws Exception {
    // if bean is disposable
    if (isDisposableBean) {
      ((DisposableBean) bean).destroy();
    }

    // 2. having custom destroy method
    if (destroyMethodName != null && !destroyMethodName.isEmpty() &&
      !(isDisposableBean && "destroy".equals(destroyMethodName))) {
      Method destroyMethod = bean.getClass().getMethod(destroyMethodName);
      destroyMethod.invoke(bean);
    }
  }
}
