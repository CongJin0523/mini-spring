package com.cong.beans.factory.support;

import com.cong.beans.exception.BeansException;
import com.cong.beans.factory.config.BeanDefinition;

/**
 * define basic op of register and get bean definition
 */
public interface BeanDefinitionRegistry extends AliaRegistry {
  void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

  void removeBeanDefinition(String beanName) throws BeansException;

  BeanDefinition getBeanDefinition(String beanName) throws BeansException;

  boolean containsBeanDefinition(String beanName);

  String[] getBeanDefinitionNames();

  int getBeanDefinitionCount();
}
