package com.cong.beans.factory.config;

import com.cong.beans.factory.PropertyValues;

import java.util.List;

public interface BeanDefinition {
  String SCOPE_SINGLETON = "singleton";
  String SCOPE_PROTOTYPE = "prototype";

  Class<?> getBeanClass();
  String getScope();
  void setScope(String scope);
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

