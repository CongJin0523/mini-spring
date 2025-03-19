package com.cong.beans.factory;

import com.cong.beans.exception.BeansException;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * retrieving multiple beans
 * query bean
 */
public interface ListableBeanFactory extends BeanFactory{
  boolean containsBeanDefinition(String beanName);
  int getBeanDefinitionCount();
  String[] getBeanDefinitionNames();
  String[] getBeanNamesForType(Class<?> type);
  <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;
  Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;
  <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws BeansException;
}

