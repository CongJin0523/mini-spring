package com.cong.beans.factory.config;

/**
 * register singleton bean
 * basic op of manage singleton bean
 */
public interface SingletonBeanRegistry {

  void registerSingleton(String beanName, Object singletonObject);
  Object getSingleton(String beanName);
  boolean containsSingleton(String beanName);
  String[] getSingletonNames();
  int getSingletonCount();
}
