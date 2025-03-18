package com.cong.beans.factory.config;

import java.util.ArrayList;
import java.util.List;

/**
 * wrapper class
 * use to save bean definition, constructor arguments and method message
 */
public class BeanDefinitionHolder {
  private final BeanDefinition beanDefinition;
  private final String beanName;
  private final List<ConstructorArgumentValue> constructorArgumentValues;
  private final List<PropertyValue> propertyValues;

  public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
    this.beanDefinition = beanDefinition;
    this.beanName = beanName;
    this.constructorArgumentValues = new ArrayList<>();
    this.propertyValues = new ArrayList<>();
  }

  public BeanDefinition getBeanDefinition() {
    return this.beanDefinition;
  }

  public String getBeanName() {
    return this.beanName;
  }

  public void addConstructorArgumentValue(ConstructorArgumentValue argumentValue) {
    this.constructorArgumentValues.add(argumentValue);
  }

  public List<ConstructorArgumentValue> getConstructorArgumentValues() {
    return new ArrayList<>(this.constructorArgumentValues);
  }

  public void addPropertyValue(PropertyValue propertyValue) {
    this.propertyValues.add(propertyValue);
  }


  public List<PropertyValue> getPropertyValues() {
    return new ArrayList<>(this.propertyValues);
  }
}
