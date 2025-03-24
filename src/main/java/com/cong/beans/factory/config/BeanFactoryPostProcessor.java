package com.cong.beans.factory.config;

import com.cong.beans.exception.BeansException;

/**
 * after all bean definition loaded, before bean instantiated
 * edit bean property
 * edit bean scope
 * edit bean dependence
 */
public interface BeanFactoryPostProcessor {
  void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
