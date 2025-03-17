package com.cong.beans.factory.support;

import com.cong.beans.factory.ObjectFactory;
import com.cong.beans.factory.config.BeanDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory {
  private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
  private volatile List<String> beanDefinitionNames = new ArrayList<>(256);

  //three layer cache to load beaning
  private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
  private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);
  private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>(16);
}
