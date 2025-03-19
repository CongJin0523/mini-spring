package com.cong.beans.factory;

import com.cong.beans.exception.BeansException;

public interface BeanFactory {
  /**
   * get bean by name
   * @param name
   * @return bean instance
   */
  Object getBean(String name);

  /**
   * get bean by name and type
   * @param name
   * @param requiredType
   * @return bean instance
   * @param <T>
   */
  <T> T getBean(String name, Class<T> requiredType);

  /**
   * get bean by type
   * @param requiredType
   * @return
   * @param <T>
   */
  <T> T getBean(Class<T> requiredType);

  /**
   * if including bean with name
   * @param name
   * @return
   */
  boolean containsBean(String name);

  /**
   * if it is singleton
    * @param name
   * @return
   */
  boolean isSingleton(String name);

  /**
   * if prototype
    * @param name
   * @return
   */
  boolean isPrototype(String name);

  /**
   * get bean type
   * @param name
   * @return bean's type or null if it's not exists
   * @throws BeansException
   */
  Class<?> getType(String name) throws BeansException;
}
