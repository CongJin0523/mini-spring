package com.cong.util;

import com.cong.beans.exception.BeansException;
import com.cong.beans.factory.BeanFactory;
import com.cong.beans.factory.config.PropertyValue;

import java.lang.reflect.Method;

/**
 * bean tool class
 */
public class BeanUtils {
  /**
   * set bean property
   */
  public static void setProperty(Object bean, PropertyValue propertyValue, BeanFactory beanFactory) throws BeansException {
    String propertyName = propertyValue.getName();
    Object value = propertyValue.getValue();
    Class<?> type = propertyValue.getType();

    try {
      // if value is string, but type is not, try to get bean reference from beanFactory
      if (value instanceof String && type != String.class) {
        String beanName = (String) value;
        // get from bean factory
        value = beanFactory.getBean(beanName, type);
      }

      String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
      Method setter = bean.getClass().getMethod(methodName, type);
      // make setter method can be accessible
      setter.setAccessible(true);
      setter.invoke(bean, value);
    } catch (Exception e) {
      throw new BeansException("Error setting property '" + propertyName + "' to bean", e);
    }
  }
}
