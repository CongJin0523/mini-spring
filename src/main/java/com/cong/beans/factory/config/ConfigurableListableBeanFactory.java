package com.cong.beans.factory.config;

import com.cong.beans.exception.BeansException;
import com.cong.beans.factory.AutowireCapableBeanFactory;
import com.cong.beans.factory.ListableBeanFactory;

/**
 *  ConfigurableListableBeanFactory
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, ConfigurableBeanFactory, AutowireCapableBeanFactory {
  BeanDefinition getBeanDefinition(String beanName) throws BeansException;

  /**
   *
   * @throws BeansException
   */
  void preInstantiateSingletons() throws BeansException;

  /**
   * ensure all not lazy loading bean instantiated
   * @throws BeansException
   */
  void ensureAllSingletonsInstantiated() throws BeansException;

}
