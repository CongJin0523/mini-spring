package com.cong.beans.factory.support;

import com.cong.beans.exception.BeansException;
import com.cong.beans.factory.ObjectFactory;
import com.cong.beans.factory.config.BeanDefinition;
import com.cong.beans.factory.config.BeanPostProcessor;
import com.cong.beans.factory.config.ConfigurableBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanFactory extends SimpleAliasRegistry implements ConfigurableBeanFactory {
  private static final Logger logger = LoggerFactory.getLogger(AbstractBeanFactory.class);
  private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
  private ClassLoader beanClassLoader = Thread.currentThread().getContextClassLoader();
  private ConfigurableBeanFactory parentBeanFactory;

  protected final Set<String> singletonsCurrentlyInCreation =
    Collections.newSetFromMap(new ConcurrentHashMap<>(16));
  private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>(16);
  private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);
  private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

  /**
   * before creating singleton
   */
  protected void beforeSingletonCreation(String beanName) {
    if (!this.singletonsCurrentlyInCreation.add(beanName)) {
      throw new BeansException("Bean with name '" + beanName + "' is currently in creation");
    }
  }

  /**
   * after creating singleton
   */
  protected void afterSingletonCreation(String beanName) {
    if (!this.singletonsCurrentlyInCreation.remove(beanName)) {
      throw new BeansException("Bean with name '" + beanName + "' is not in creation");
    }
  }

  /**
   * add singleton factory
   */
  protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
    synchronized (this.singletonObjects) {
      if (!this.singletonObjects.containsKey(beanName)) {
        this.singletonFactories.put(beanName, singletonFactory);
        this.earlySingletonObjects.remove(beanName);
      }
    }
  }

  /**
   * register singleton object
   */
  @Override
  public void registerSingleton(String beanName, Object singletonObject) {
    synchronized (this.singletonObjects) {
      this.singletonObjects.put(beanName, singletonObject);
      this.singletonFactories.remove(beanName);
      this.earlySingletonObjects.remove(beanName);
      logger.debug("Registered singleton bean named '{}'", beanName);
    }
  }
  /**
   * add singleton
   */
  public void addSingleton(String beanName, Object singletonObject) {
    registerSingleton(beanName, singletonObject);
  }

  /**
   * get singleton bean
   */
  public Object getSingleton(String beanName) {
    // get from 1st cache
    Object singletonObject = this.singletonObjects.get(beanName);
    if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
      synchronized (this.singletonObjects) {
        // get from 2nd cache
        singletonObject = this.earlySingletonObjects.get(beanName);
        if (singletonObject == null) {
          // get from factory
          ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
          if (singletonFactory != null) {
            singletonObject = singletonFactory.getObject();
            this.earlySingletonObjects.put(beanName, singletonObject);
            this.singletonFactories.remove(beanName);
          }
        }
      }
    }
    return singletonObject;
  }

  /**
   * if in creating
   */
  protected boolean isSingletonCurrentlyInCreation(String beanName) {
    return this.singletonsCurrentlyInCreation.contains(beanName);
  }

  /**
   * get reference from 2nd cache
   */
  protected Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean) {
    return bean;
  }

  @Override
  public Object getBean(String name) throws BeansException {
    return doGetBean(name, null);
  }

  @Override
  public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
    return doGetBean(name, requiredType);
  }

  @SuppressWarnings("unchecked")
  protected <T> T doGetBean(String name, Class<T> requiredType) {
    String beanName = transformedBeanName(name);
    Object bean;

    // get bean definition
    BeanDefinition beanDefinition = getBeanDefinition(beanName);
    if (beanDefinition == null) {
      throw new BeansException("No bean named '" + beanName + "' is defined");
    }

    // deal according scope
    if (beanDefinition.isSingleton()) {
      // singleton ben, get from cache
      bean = getSingleton(beanName);
      if (bean == null) {
        bean = createBean(beanName, beanDefinition);
        addSingleton(beanName, bean);
      }
    } else if (beanDefinition.isPrototype()) {
      // prototype, creating a new instance each time
      bean = createBean(beanName, beanDefinition);
    } else {
      throw new BeansException("Unsupported scope '" + beanDefinition.getScope() + "' for bean '" + beanName + "'");
    }

    // check Type
    if (requiredType != null && !requiredType.isInstance(bean)) {
      throw new BeansException("Bean named '" + name + "' is expected to be of type '" + requiredType + "' but was actually of type '" + bean.getClass().getName() + "'");
    }

    return (T) bean;
  }

  protected abstract Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException;

  protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

  @Override
  public void setBeanClassLoader(ClassLoader beanClassLoader) {
    this.beanClassLoader = (beanClassLoader != null ? beanClassLoader : Thread.currentThread().getContextClassLoader());
  }

  @Override
  public ClassLoader getBeanClassLoader() {
    return this.beanClassLoader;
  }

  @Override
  public void setParentBeanFactory(ConfigurableBeanFactory parentBeanFactory) {
    if (this.parentBeanFactory != null) {
      throw new IllegalStateException("Already has a parent BeanFactory");
    }
    this.parentBeanFactory = parentBeanFactory;
  }

  @Override
  public ConfigurableBeanFactory getParentBeanFactory() {
    return this.parentBeanFactory;
  }


  @Override
  public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
    this.beanPostProcessors.remove(beanPostProcessor);
    this.beanPostProcessors.add(beanPostProcessor);
    logger.debug("Added bean post processor: {}", beanPostProcessor);
  }

  @Override
  public List<BeanPostProcessor> getBeanPostProcessors() {
    return this.beanPostProcessors;
  }

  /**
   * deal alias
   */
  protected String transformedBeanName(String name) {
    return canonicalName(name);
  }

  /**
   * template to get bean
   */
  protected abstract Object doGetBean(String beanName) throws BeansException;

  /**
   * apply post process before instantiation
   */
  protected abstract Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
    throws BeansException;

  /**
   *  apply post process after bean instantiation
   */
  protected abstract Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
    throws BeansException;


}
