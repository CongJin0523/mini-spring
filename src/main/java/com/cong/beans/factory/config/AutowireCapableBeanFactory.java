package com.cong.beans.factory.config;

import com.cong.beans.exception.BeansException;
import com.cong.beans.factory.BeanFactory;

/**
 *
 */
public interface AutowireCapableBeanFactory extends BeanFactory {
  int AUTOWIRE_NO = 0;
  int AUTOWIRE_BY_NAME = 1;
  int AUTOWIRE_BY_TYPE = 2;
  int AUTOWIRE_CONSTRUCTOR = 3;

  <T> T createBean(Class<T> beanClass) throws BeansException;
  void autowireBean(Object existingBean) throws BeansException;
  Object configureBean(Object existingBean, String beanName) throws BeansException;
  Object resolveDependency(Class<?> descriptor, String beanName) throws BeansException;

  ConfigurableBeanFactory getBeanFactory();

}
