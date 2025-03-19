package com.cong.beans.factory;

/**
 * define the relationship of bean factory
 */
public interface HierarchicalBeanFactory extends BeanFactory{
  BeanFactory getParentBeanFactory();
  boolean containsLocalBean(String name);
}
