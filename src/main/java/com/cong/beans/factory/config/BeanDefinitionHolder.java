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
  private final String[] aliases;

  public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
    this(beanDefinition, beanName, null);
  }

  public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName, String[] aliases) {
    this.beanDefinition = beanDefinition;
    this.beanName = beanName;
    this.aliases = aliases;
  }

  public BeanDefinition getBeanDefinition() {
    return beanDefinition;
  }

  public String getBeanName() {
    return beanName;
  }

  public String[] getAliases() {
    return aliases;
  }
}
