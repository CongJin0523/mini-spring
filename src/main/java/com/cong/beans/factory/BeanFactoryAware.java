package com.cong.beans.factory;

/**
 * known its beanFactory
 */
public interface BeanFactoryAware extends Aware {
  void setBeanFactory(BeanFactory beanFactory);
}
