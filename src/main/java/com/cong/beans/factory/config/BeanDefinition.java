package com.cong.beans.factory.config;

public interface BeanDefinition {
  String SCOPE_SINGLETON = "singleton";
  String SCOPE_PROTOTYPE = "prototype";

  Class<?> getBeanClass();
  String getScope();
  boolean isSingleton();
  boolean isPrototype();
  String getInitMethodName();
  void setInitMethodName(String initMethodName);
  String getDestroyMethodName();
  void setDestroyMethodName(String destroyMethodName);
}

