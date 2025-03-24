package com.cong.beans.factory.annotation;

import com.cong.beans.exception.BeansException;
import com.cong.beans.factory.BeanFactory;
import com.cong.beans.factory.BeanFactoryAware;
import com.cong.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.cong.beans.factory.config.PropertyValues;

import java.lang.reflect.Field;

public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {
  private BeanFactory beanFactory;
  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }
  @Override
  public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
    return null;
  }

  @Override
  public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
    return true;
  }

  @Override
  public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
    Class<?> clazz = bean.getClass();
    Field[] fields = clazz.getDeclaredFields();

    for (Field field : fields) {
      Autowired autowired = field.getAnnotation(Autowired.class);
      if (autowired != null) {
        String autowiredBeanName = field.getName();
        Object autowiredBean = beanFactory.getBean(autowiredBeanName);

        if (autowiredBean == null && autowired.required()) {
          throw new BeansException("Could not autowire required field: " + field.getName());
        }

        if (autowiredBean != null) {
          field.setAccessible(true);
          try {
            field.set(bean, autowiredBean);
          } catch (IllegalAccessException e) {
            throw new BeansException("Could not autowire field: " + field.getName(), e);
          }
        }
      }
    }

    return pvs;
  }
}
