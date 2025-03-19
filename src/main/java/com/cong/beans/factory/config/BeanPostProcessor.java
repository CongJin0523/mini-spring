package com.cong.beans.factory.config;

import com.cong.beans.exception.BeansException;

public interface BeanPostProcessor {
  default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }
  default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }
}

