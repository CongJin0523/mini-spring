package com.cong.context.annotation;

import com.cong.beans.factory.support.BeanDefinitionRegistry;
import com.cong.core.env.Environment;
import com.cong.core.io.ResourceLoader;
import com.cong.util.ClassUtils;

public class StandardConditionContext implements ConditionContext {
  private final BeanDefinitionRegistry registry;
  private final Environment environment;
  private final ResourceLoader resourceLoader;
  private final ClassLoader classLoader;

  public StandardConditionContext(BeanDefinitionRegistry registry, Environment environment,
                                  ResourceLoader resourceLoader) {
    this.registry = registry;
    this.environment = environment;
    this.resourceLoader = resourceLoader;
    this.classLoader = ClassUtils.getDefaultClassLoader();
  }

  @Override
  public BeanDefinitionRegistry getRegistry() {
    return this.registry;
  }

  @Override
  public ClassLoader getClassLoader() {
    return this.classLoader;
  }

  @Override
  public Environment getEnvironment() {
    return this.environment;
  }

  @Override
  public ResourceLoader getResourceLoader() {
    return this.resourceLoader;
  }
}
