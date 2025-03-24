package com.cong.beans.factory.config;

import java.util.List;

public interface BeanDefinition {
  String SCOPE_SINGLETON = "singleton";
  String SCOPE_PROTOTYPE = "prototype";

  void setBeanClass(Class<?> beanClass);
  Class<?> getBeanClass();
  String getBeanClassName();
  String getScope();
  void setScope(String scope);
  // lazyInit
  void setLazyInit(boolean lazyInit);
  boolean isLazyInit();

  boolean isSingleton();
  boolean isPrototype();
  //init
  String getInitMethodName();
  void setInitMethodName(String initMethodName);
  //destroy
  String getDestroyMethodName();
  void setDestroyMethodName(String destroyMethodName);
  // constructor
  List<ConstructorArgumentValue> getConstructorArgumentValues();
  void addConstructorArgumentValue(ConstructorArgumentValue constructorArgumentValue);
  boolean hasConstructorArgumentValues();
  //property
  PropertyValues getPropertyValues();

  void setPropertyValues(PropertyValues propertyValues);

  void addPropertyValue(PropertyValue propertyValue);
}

