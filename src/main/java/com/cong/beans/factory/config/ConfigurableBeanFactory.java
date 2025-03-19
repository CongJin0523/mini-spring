package com.cong.beans.factory.config;

import com.cong.beans.factory.HierarchicalBeanFactory;

import java.util.List;

/**
 * additional configuration options for a BeanFactory
 * managing bean scopes, handling property placeholders, and supporting bean post-processors
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry{
  String SCOPE_SINGLETON = "singleton";
  String SCOPE_PROTOTYPE = "prototype";

  void setParentBeanFactory(ConfigurableBeanFactory parentBeanFactory);
  void setBeanClassLoader(ClassLoader beanClassLoader);
  ClassLoader getBeanClassLoader();
  void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);
  List<BeanPostProcessor> getBeanPostProcessors();
  int getBeanPostProcessorCount();
  void registerDependentBean(String beanName, String dependentBeanName);
  String[] getDependentBeans(String beanName);
  String[] getDependenciesForBean(String beanName);
  void destroySingletons();
}
