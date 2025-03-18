package com.cong.beans.factory.support;

import com.cong.beans.exception.BeansException;
import com.cong.beans.factory.BeanFactory;
import com.cong.beans.factory.ObjectFactory;
import com.cong.beans.factory.config.BeanDefinition;
import com.cong.beans.factory.config.BeanDefinitionHolder;
import com.cong.beans.factory.config.ConstructorArgumentValue;
import com.cong.beans.factory.config.PropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * bean singleton register, alias , DI, circular dependence deal
 */
public class DefaultListableBeanFactory extends SimpleAliasRegistry implements BeanFactory {

  private static final Logger logger = LoggerFactory.getLogger(DefaultListableBeanFactory.class);


  //three layer cache to load beaning
  //1st
  private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
  //2nd
  // save beans which is already instanced but not init or constructed
  private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);
  //3rd
  // save bean factory
  private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>(16);

  //bean name in creating
  private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

  // bean definition
  private final Map<String, BeanDefinitionHolder> beanDefinitionMap = new ConcurrentHashMap<>(256);


  @Override
  public Object getBean(String name) throws BeansException {
    String beanName = canonicalName(name);
    BeanDefinitionHolder holder = getBeanDefinitionHolder(beanName);
    BeanDefinition beanDefinition = holder.getBeanDefinition();

    if (beanDefinition.isSingleton()) {
      // check 1st cache
      Object singleton = singletonObjects.get(beanName);
      if (singleton != null) {
        logger.debug("Found singleton bean '{}' in primary cache", beanName);
        return singleton;
      }

      // check 2nd cache, avoid circular dependence
      singleton = getSingletonEarly(beanName);
      if (singleton != null) {
        return singleton;
      }

      // mark bean is creating
      beforeSingletonCreation(beanName);
      try {
        // creating bean
        singleton = createBean(beanName, holder);
        // put the entir bean in 1st cache
        addSingleton(beanName, singleton);
      } finally {
        // remove creating mark
        afterSingletonCreation(beanName);
      }
      return singleton;
    } else {
      // if it is prototype, creating new bean
      Object prototype = createBean(beanName, holder);
      logger.debug("Instantiated prototype bean '{}'", beanName);
      return prototype;
    }
  }



  public BeanDefinitionHolder getBeanDefinitionHolder(String name) throws BeansException {
    String beanName = canonicalName(name);
    BeanDefinitionHolder holder = beanDefinitionMap.get(beanName);
    if (holder == null) {
      throw new BeansException("No bean named '" + name + "' is defined");
    }
    return holder;
  }

  /**
   * get the early singleton
    * @param beanName
   * @return
   */
  protected Object getSingletonEarly(String beanName) {
    Object singleton = earlySingletonObjects.get(beanName);
    //the bean created is not in the 2nd cache, and is creating
    if (singleton == null && isSingletonCurrentlyInCreation(beanName)) {
      // all use singletonObjects, avoiding deadlock and consistent problem
      synchronized (this.singletonObjects) {
        //check 2nd cache again
        singleton = earlySingletonObjects.get(beanName);
        if (singleton == null) {
          // check 3rd cache, the bean factory
          ObjectFactory<?> factory = singletonFactories.get(beanName);
          if (factory != null) {
            // get from factory, and create early singleton
            singleton = factory.getObject();
            earlySingletonObjects.put(beanName, singleton);
            earlySingletonObjects.putIfAbsent(beanName, singleton);
            singletonFactories.remove(beanName);
            logger.debug("Created early reference for singleton bean '{}'", beanName);
          }
        }
      }
    }
    return singleton;
  }


  // create bean instance
  protected Object createBean(String name, BeanDefinitionHolder holder) throws BeansException {
    BeanDefinition beanDefinition = holder.getBeanDefinition();
    Class<?> beanClass = beanDefinition.getBeanClass();
    Object bean;

    try {
      //constructor inject
      List<ConstructorArgumentValue> constructorArgs = holder.getConstructorArgumentValues();
      // not empty means need to inject, considering circular reference
      if (!constructorArgs.isEmpty()) {
        Class<?>[] parameterTypes = new Class<?>[constructorArgs.size()];
        Object[] parameterValues = new Object[constructorArgs.size()];

        for (int i = 0; i < constructorArgs.size(); i++) {
          ConstructorArgumentValue argumentValue = constructorArgs.get(i);
          parameterTypes[i] = argumentValue.getType();
          //get bean through canonicalName or alias
          parameterValues[i] = getBean(argumentValue.getValue().toString());
        }

        //user reflection to get constructor
        Constructor<?> constructor = beanClass.getDeclaredConstructor(parameterTypes);
        bean = constructor.newInstance(parameterValues);
        logger.debug("Created bean '{}' using constructor injection", name);
      } else {
        bean = beanClass.getDeclaredConstructor().newInstance();
        logger.debug("Created bean '{}' using default constructor", name);
      }

      //during creating and singleton, put it in 3rd cache, cannot circular reference in constructor
      if (beanDefinition.isSingleton() && isSingletonCurrentlyInCreation(name)) {
        singletonFactories.put(name, () -> bean);
        logger.debug("Added factory for singleton bean '{}' to third-level cache", name);
      }

      //setting injecting
      List<PropertyValue> propertyValues = holder.getPropertyValues();
      if (!propertyValues.isEmpty()) {
        for (PropertyValue propertyValue : propertyValues) {
          String propertyName = propertyValue.getName();
          Object value = propertyValue.getValue();

          // if properties is other bean
          if (value instanceof String && containsBean(value.toString())) {
            value = getBean(value.toString());
          }

          // PropertyDescriptor injecting
          PropertyDescriptor pd = new PropertyDescriptor(propertyName, beanClass);
          Method writeMethod = pd.getWriteMethod();
          if (writeMethod != null) {
            writeMethod.invoke(bean, value);
            logger.debug("Injected property '{}' of bean '{}'", propertyName, name);
          }
        }
      }

      //after creating, init
      String initMethodName = beanDefinition.getInitMethodName();
      if (initMethodName != null && !initMethodName.isEmpty()) {
        Method initMethod = beanClass.getMethod(initMethodName);
        initMethod.invoke(bean);
        logger.debug("Invoked init-method '{}' of bean '{}'", initMethodName, name);
      }

    } catch (Exception e) {
      throw new BeansException("Error creating bean with name '" + name + "'", e);
    }
    return bean;
  }

  // method used in create bean
  // mark a bean is creating
  protected void beforeSingletonCreation(String beanName) {
    if (!singletonsCurrentlyInCreation.add(beanName)) {
      throw new BeansException(
        "Circular reference detected during bean creation for singleton '" + beanName + "'");
    }
  }
  // remove the creating mark
  protected void afterSingletonCreation(String beanName) {
    if (!singletonsCurrentlyInCreation.remove(beanName)) {
      throw new BeansException(
        "Singleton '" + beanName + "' isn't currently in creation");
    }
  }

  // check if bean is creating
  protected boolean isSingletonCurrentlyInCreation(String beanName) {
    return singletonsCurrentlyInCreation.contains(beanName);
  }

  // add bean to 1st cache(done)
  protected void addSingleton(String beanName, Object singleton) {
    synchronized (this.singletonObjects) {
      this.singletonObjects.put(beanName, singleton);
      earlySingletonObjects.remove(beanName);
      singletonFactories.remove(beanName);
      logger.debug("Added singleton bean '{}' to primary cache", beanName);
    }
  }


  @Override
  public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
    Object bean = getBean(name);
    if (requiredType != null && !requiredType.isInstance(bean)) {
      throw new BeansException(
        "Bean named '" + name + "' is expected to be of type '" + requiredType.getName() +
          "' but was actually of type '" + bean.getClass().getName() + "'");
    }
    return requiredType.cast(bean);
  }


  @Override
  public <T> T getBean(Class<T> requiredType) throws BeansException {
    // search beanDefinitionMap，find bean match requiredType
    for (Map.Entry<String, BeanDefinitionHolder> entry : beanDefinitionMap.entrySet()) {
      if (requiredType.isAssignableFrom(entry.getValue().getBeanDefinition().getBeanClass())) {
        return requiredType.cast(getBean(entry.getKey()));
      }
    }
    throw new BeansException("No qualifying bean of type '" + requiredType.getName() + "' available");
  }

  @Override
  public boolean containsBean(String name) {
    String beanName = canonicalName(name);
    return beanDefinitionMap.containsKey(beanName);
  }


  @Override
  public boolean isSingleton(String name) throws BeansException {
    String beanName = canonicalName(name);
    BeanDefinition beanDefinition = getBeanDefinitionHolder(beanName).getBeanDefinition();
    return beanDefinition.isSingleton();
  }

  @Override
  public boolean isPrototype(String name) throws BeansException {
    String beanName = canonicalName(name);
    BeanDefinition beanDefinition = getBeanDefinitionHolder(beanName).getBeanDefinition();
    return beanDefinition.isPrototype();
  }

  public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
    BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, name);
    beanDefinitionMap.put(name, holder);
    logger.debug("Registered bean definition for bean named '{}'", name);
  }

  public void registerSingleton(String name, Object singletonObject) {
    String beanName = canonicalName(name);
    singletonObjects.put(beanName, singletonObject);
    logger.debug("Registered singleton bean named '{}'", beanName);
  }

  //destory all singletons, using before closing
  public void destroySingletons() {
    for (Map.Entry<String, Object> entry : singletonObjects.entrySet()) {
      String beanName = entry.getKey();
      Object bean = entry.getValue();
      BeanDefinitionHolder holder = beanDefinitionMap.get(beanName);

      if (holder != null) {
        BeanDefinition beanDefinition = holder.getBeanDefinition();
        String destroyMethodName = beanDefinition.getDestroyMethodName();
        if (destroyMethodName != null && !destroyMethodName.isEmpty()) {
          try {
            Method destroyMethod = bean.getClass().getMethod(destroyMethodName);
            destroyMethod.invoke(bean);
            logger.debug("Invoked destroy-method '{}' of bean '{}'", destroyMethodName, beanName);
          } catch (Exception e) {
            logger.error("Error invoking destroy-method '{}' of bean '{}'", destroyMethodName, beanName, e);
          }
        }
      }
    }
    singletonObjects.clear();
    logger.debug("Destroyed all singleton beans");
  }

}
