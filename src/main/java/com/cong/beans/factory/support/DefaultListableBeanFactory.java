package com.cong.beans.factory.support;

import com.cong.beans.exception.BeansException;
import com.cong.beans.factory.*;
import com.cong.beans.factory.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * bean singleton register, alias , DI, circular dependence deal
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
  implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {

  private static final Logger logger = LoggerFactory.getLogger(DefaultListableBeanFactory.class);

  /** Map of bean definition objects, keyed by bean name */
  private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

  /** List of bean definition names, in registration order */
  private volatile List<String> beanDefinitionNames = new ArrayList<>(256);

  /** Map from bean name to merged bean definition */
  private final Map<String, BeanDefinition> mergedBeanDefinitions = new ConcurrentHashMap<>(256);

  /** Names of beans that are currently in creation */
  private final Set<String> singletonsCurrentlyInCreation =
    Collections.newSetFromMap(new ConcurrentHashMap<>(16));

  /** Cache of singleton objects: bean name to bean instance */
  private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

  /** Cache of early singleton objects: bean name to bean instance */
  private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);

  /** Cache of singleton factories: bean name to ObjectFactory */
  private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>(16);

  private ClassLoader beanClassLoader = Thread.currentThread().getContextClassLoader();
  private ConfigurableBeanFactory parentBeanFactory;
  private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);
  private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);
  private final Set<String> beansInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));
  private final Map<String, Set<String>> dependencyGraph = new ConcurrentHashMap<>(64);


  @Override
  public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
    Objects.requireNonNull(beanName, "Bean name must not be null");
    Objects.requireNonNull(beanDefinition, "BeanDefinition must not be null");

    // check if having the old definition
    BeanDefinition oldBeanDefinition = this.beanDefinitionMap.get(beanName);
    if (oldBeanDefinition != null) {
      // clearing when scope changes
      if (!Objects.equals(oldBeanDefinition.getScope(), beanDefinition.getScope())) {
        cleanupSingletonCache(beanName);
        // remove old definition
        this.beanDefinitionMap.remove(beanName);
        this.mergedBeanDefinitions.remove(beanName);
        // dealing alias
        String[] aliases = getAliases(beanName);
        for (String alias : aliases) {
          cleanupSingletonCache(alias);
          this.mergedBeanDefinitions.remove(alias);
        }
      }
    }

    this.beanDefinitionMap.put(beanName, beanDefinition);

    // new bean definition, adding to bean definition list
    if (!this.beanDefinitionNames.contains(beanName)) {
      this.beanDefinitionNames.add(beanName);
    }

    logger.debug("Registered bean definition for bean named '{}'", beanName);
  }

  @Override
  public void removeBeanDefinition(String beanName) throws BeansException {
    if (!containsBeanDefinition(beanName)) {
      throw new BeansException("No bean named '" + beanName + "' is defined");
    }
    this.beanDefinitionMap.remove(beanName);
    this.beanDefinitionNames.remove(beanName);
    logger.debug("Removed bean definition for bean named '{}'", beanName);
  }

  @Override
  public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
    String canonicalName = canonicalName(beanName);
    BeanDefinition bd = this.beanDefinitionMap.get(canonicalName);
    if (bd == null) {
      if (getParentBeanFactory() instanceof DefaultListableBeanFactory) {
        return ((DefaultListableBeanFactory) getParentBeanFactory()).getBeanDefinition(canonicalName);
      }
      throw new BeansException("No bean named '" + beanName + "' is defined");
    }
    // if having merge bean definition, return merge Bd
    BeanDefinition mergedBd = this.mergedBeanDefinitions.get(canonicalName);
    if (mergedBd != null) {
      return mergedBd;
    }
    return bd;
  }

  @Override
  public boolean containsBeanDefinition(String beanName) {
    return this.beanDefinitionMap.containsKey(beanName);
  }

  @Override
  public String[] getBeanDefinitionNames() {
    return this.beanDefinitionNames.toArray(new String[0]);
  }

  @Override
  public int getBeanDefinitionCount() {
    return this.beanDefinitionMap.size();
  }

  @Override
  public void registerSingleton(String beanName, Object singletonObject) {
    this.singletonObjects.put(beanName, singletonObject);
    logger.debug("Registered singleton bean named '{}'", beanName);
  }

  @Override
  public Object getSingleton(String beanName) {
    return getSingleton(beanName, true);
  }

  protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    // 1st cache
    Object singletonObject = this.singletonObjects.get(beanName);

    if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
      synchronized (this.singletonObjects) {
        // 2nd cache
        singletonObject = this.earlySingletonObjects.get(beanName);

        if (singletonObject == null && allowEarlyReference) {
          // 3rd cache
          ObjectFactory<?> factory = this.singletonFactories.get(beanName);
          if (factory != null) {
            // get from 3rd
            singletonObject = factory.getObject();
            // put into 2nd
            this.earlySingletonObjects.put(beanName, singletonObject);
            // remove from 3rd
            this.singletonFactories.remove(beanName);
          }
        }
      }
    }
    return singletonObject;
  }

  @Override
  public boolean containsSingleton(String beanName) {
    return this.singletonObjects.containsKey(beanName);
  }

  @Override
  public String[] getSingletonNames() {
    return this.singletonObjects.keySet().toArray(new String[0]);
  }

  @Override
  public int getSingletonCount() {
    return this.singletonObjects.size();
  }

  @Override
  public void preInstantiateSingletons() throws BeansException {
    List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);
    for (String beanName : beanNames) {
      BeanDefinition beanDefinition = getBeanDefinition(beanName);
      if (beanDefinition.isSingleton()) {
        getBean(beanName);
        logger.debug("Pre-instantiated singleton bean named '{}'", beanName);
      }
    }
  }

  @Override
  public void ensureAllSingletonsInstantiated() throws BeansException {
    preInstantiateSingletons();
  }

  @Override
  public Class<?> getType(String name) throws BeansException {
    String beanName = transformedBeanName(name);

    // check if it is instantiated bean
    Object singleton = getSingleton(beanName);
    if (singleton != null) {
      return singleton.getClass();
    }

    // check definition
    BeanDefinition beanDefinition = getBeanDefinition(beanName);
    if (beanDefinition == null) {
      return null;
    }

    return beanDefinition.getBeanClass();
  }

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
  public boolean containsLocalBean(String name) {
    String beanName = transformedBeanName(name);
    return containsBeanDefinition(beanName) || containsSingleton(beanName);
  }

  @Override
  public boolean containsBean(String name) {
    String beanName = transformedBeanName(name);
    if (containsSingleton(beanName) || containsBeanDefinition(beanName)) {
      return true;
    }
    // check parent factory
    BeanFactory parentBeanFactory = getParentBeanFactory();
    return parentBeanFactory != null && parentBeanFactory.containsBean(beanName);
  }

  @Override
  public boolean isSingleton(String name) throws BeansException {
    String beanName = transformedBeanName(name);

    // check bean definition
    if (containsBeanDefinition(beanName)) {
      BeanDefinition beanDefinition = getBeanDefinition(beanName);
      if (beanDefinition != null) {
        return beanDefinition.isSingleton();
      }
    }

    // check factory
    BeanFactory parentBeanFactory = getParentBeanFactory();
    return parentBeanFactory != null && parentBeanFactory.isSingleton(beanName);
  }

  @Override
  public boolean isPrototype(String name) throws BeansException {
    String beanName = transformedBeanName(name);

    // check bean definition
    if (containsBeanDefinition(beanName)) {
      BeanDefinition beanDefinition = getBeanDefinition(beanName);
      if (beanDefinition != null) {
        return beanDefinition.isPrototype();
      }
    }

    // check parent factory
    BeanFactory parentBeanFactory = getParentBeanFactory();
    return parentBeanFactory != null && parentBeanFactory.isPrototype(beanName);
  }

  @Override
  public void registerDependentBean(String beanName, String dependentBeanName) {
    String canonicalName = transformedBeanName(beanName);
    synchronized (this.dependencyGraph) {
      Set<String> dependencies = this.dependencyGraph.computeIfAbsent(
        canonicalName, k -> new LinkedHashSet<>());
      dependencies.add(dependentBeanName);
    }
  }

  @Override
  public String[] getDependentBeans(String beanName) {
    Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
    if (dependentBeans == null) {
      return new String[0];
    }
    return dependentBeans.toArray(new String[0]);
  }

  @Override
  public String[] getDependenciesForBean(String beanName) {
    Set<String> dependencies = this.dependenciesForBeanMap.get(beanName);
    if (dependencies == null) {
      return new String[0];
    }
    return dependencies.toArray(new String[0]);
  }

  @Override
  public String[] getBeanNamesForType(Class<?> type) {
    List<String> result = new ArrayList<>();

    // check instantiated bean
    for (Map.Entry<String, Object> entry : singletonObjects.entrySet()) {
      if (type.isInstance(entry.getValue())) {
        result.add(entry.getKey());
      }
    }

    // check bean definition
    for (String beanName : beanDefinitionNames) {
      if (result.contains(beanName)) {
        continue;
      }
      BeanDefinition beanDefinition = getBeanDefinition(beanName);
      Class<?> beanClass = beanDefinition.getBeanClass();
      if (type.isAssignableFrom(beanClass)) {
        result.add(beanName);
      }
    }

    return result.toArray(new String[0]);
  }

  @Override
  public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
    Map<String, T> result = new LinkedHashMap<>();
    for (String beanName : beanDefinitionNames) {
      BeanDefinition beanDefinition = getBeanDefinition(beanName);
      Class<?> beanClass = beanDefinition.getBeanClass();
      if (type.isAssignableFrom(beanClass)) {
        @SuppressWarnings("unchecked")
        T bean = (T) getBean(beanName);
        result.put(beanName, bean);
      }
    }
    return result;
  }

  @Override
  public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
    Map<String, Object> result = new LinkedHashMap<>();
    for (String beanName : beanDefinitionNames) {
      BeanDefinition beanDefinition = getBeanDefinition(beanName);
      Class<?> beanClass = beanDefinition.getBeanClass();
      if (beanClass.isAnnotationPresent(annotationType)) {
        result.put(beanName, getBean(beanName));
      }
    }
    return result;
  }

  @Override
  public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws BeansException {
    BeanDefinition beanDefinition = getBeanDefinition(beanName);
    Class<?> beanClass = beanDefinition.getBeanClass();
    return beanClass.getAnnotation(annotationType);
  }

  @Override
  public <T> T getBean(Class<T> requiredType) throws BeansException {
    String[] beanNames = getBeanNamesForType(requiredType);
    if (beanNames.length == 0) {
      throw new BeansException("No bean of type '" + requiredType.getName() + "' is defined");
    }
    if (beanNames.length > 1) {
      throw new BeansException("More than one bean of type '" + requiredType.getName() + "' is defined: " +
        String.join(", ", beanNames));
    }
    return getBean(beanNames[0], requiredType);
  }

  /**
   * get bean definition holder
    * @param beanName
   * @return BeanDefintionHolder
   * @throws BeansException
   */
  public BeanDefinitionHolder getBeanDefinitionHolder(String beanName) throws BeansException {
    BeanDefinition beanDefinition = getBeanDefinition(beanName);
    String[] aliases = getAliases(beanName);
    return new BeanDefinitionHolder(beanDefinition, beanName, aliases);
  }

  /**
   *
   * get canonicalName
   */
  protected String transformedBeanName(String name) {
    // having definition
    if (containsBeanDefinition(name)) {
      return name;
    }

    // is it singleton
    if (containsSingleton(name)) {
      return name;
    }

    // alias resolve
    return resolveAlias(name);
  }

  /**
   * return canonicalName
   *
   */
  private String resolveAlias(String alias) {
    if (!isAlias(alias)) {
      return alias;
    }

    String[] aliases = super.getAliases(alias);
    return (aliases != null && aliases.length > 0) ? aliases[0] : alias;
  }

  @Override
  protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
    try {
      // if the bean have argument constructor, need to check if it needs to inject dependence
      if (beanDefinition.isSingleton() && beanDefinition.getConstructorArgumentValues() != null
        && beanDefinition.getConstructorArgumentValues().size() > 0) {
        beforeSingletonCreation(beanName);
        try {
          // create bean instance
          final Object bean = createBeanInstance(beanDefinition);

          // inject property
          populateBean(beanName, bean, beanDefinition);

          // initialize bean
          Object exposedObject = initializeBean(beanName, bean, beanDefinition);

          // put it in 1st cache
          addSingleton(beanName, exposedObject);

          return exposedObject;
        } finally {
          afterSingletonCreation(beanName);
        }
      }

      // singleton with no arguments constructor
      if (beanDefinition.isSingleton()) {
        beforeSingletonCreation(beanName);

        // create
        final Object bean = createBeanInstance(beanDefinition);

        addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, beanDefinition, bean));

        try {
          // properties set
          populateBean(beanName, bean, beanDefinition);

          //  initialize bean
          Object exposedObject = initializeBean(beanName, bean, beanDefinition);

          // put it in 1st cache
          addSingleton(beanName, exposedObject);

          return exposedObject;
        } finally {
          afterSingletonCreation(beanName);
        }
      }

      // prototype injecting
      Object bean = createBeanInstance(beanDefinition);
      populateBean(beanName, bean, beanDefinition);
      return initializeBean(beanName, bean, beanDefinition);

    } catch (Exception e) {
      throw new BeansException("Error creating bean with name '" + beanName + "'", e);
    }
  }

  protected Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean) {
    return bean;
  }

  protected void addSingletonFactory(String beanName, ObjectFactory<?> factory) {
    synchronized (this.singletonObjects) {
      if (!this.singletonObjects.containsKey(beanName)) {
        this.singletonFactories.put(beanName, factory);
        this.earlySingletonObjects.remove(beanName);
        // all alias use the same factory
        String[] aliases = getAliases(beanName);
        for (String alias : aliases) {
          if (!this.singletonObjects.containsKey(alias)) {
            this.singletonFactories.put(alias, factory);
            this.earlySingletonObjects.remove(alias);
          }
        }
      }
    }
  }

  /**
   * add singleton
   */
  public void addSingleton(String beanName, Object singletonObject) {
    synchronized (this.singletonObjects) {
      this.singletonObjects.put(beanName, singletonObject);
      // remove from 2nd, 3rd factory
      this.earlySingletonObjects.remove(beanName);
      this.singletonFactories.remove(beanName);
    }
  }

  protected boolean isSingletonCurrentlyInCreation(String beanName) {
    return this.singletonsCurrentlyInCreation.contains(beanName);
  }

  @Override
  public void destroySingletons() {
    String[] singletonNames = getSingletonNames();
    for (String singletonName : singletonNames) {
      destroySingleton(singletonName);
    }
  }

  protected void destroySingleton(String beanName) {
    // get singleton
    Object singletonInstance = getSingleton(beanName);
    if (singletonInstance != null) {
      // hav destroy method, apply destroy()
      BeanDefinition beanDefinition = getBeanDefinition(beanName);
      if (beanDefinition != null && beanDefinition.getDestroyMethodName() != null) {
        try {
          java.lang.reflect.Method destroyMethod = singletonInstance.getClass()
            .getDeclaredMethod(beanDefinition.getDestroyMethodName());
          destroyMethod.setAccessible(true);
          destroyMethod.invoke(singletonInstance);
          logger.debug("Invoked destroy method '{}' on bean '{}'",
            beanDefinition.getDestroyMethodName(), beanName);
        } catch (Exception e) {
          logger.error("Error invoking destroy method on bean '" + beanName + "'", e);
        }
      }
      // remove from 1st cache
      this.singletonObjects.remove(beanName);
      logger.debug("Destroyed singleton bean '{}'", beanName);
    }
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
  protected <T> T doGetBean(String name, Class<T> requiredType) throws BeansException {
    String canonicalName = canonicalName(name);
    Object bean = null;

    // get definition
    BeanDefinition beanDefinition = getBeanDefinition(canonicalName);
    if (beanDefinition == null && getParentBeanFactory() != null) {
      return getParentBeanFactory().getBean(name, requiredType);
    }

    // only singleton get from caches
    if (beanDefinition.isSingleton()) {
      // for 1st cache, 2cache
      bean = getSingleton(canonicalName, false);

      // in creating, try to get from 3rd cache
      if (bean == null && isSingletonCurrentlyInCreation(canonicalName)) {
        bean = getSingleton(canonicalName, true);
        if (bean != null) {
          logger.debug("Returning early reference for singleton bean '{}'", canonicalName);
        }
      }
    }

    // creating new instance if it is a prototype or not in cache
    if (bean == null) {
      try {
        bean = createBean(canonicalName, beanDefinition);
      } catch (Exception e) {
        throw new BeansException("Error creating bean '" + canonicalName + "'", e);
      }
    }

    // type check
    if (requiredType != null && !requiredType.isInstance(bean)) {
      throw new BeansException(
        "Bean named '" + name + "' is expected to be of type '" + requiredType.getName() +
          "' but was actually of type '" + bean.getClass().getName() + "'");
    }

    return (T) bean;
  }

  @Override
  public void registerAlias(String beanName, String alias) {
    if (alias.equals(beanName)) {
      removeAlias(alias);
      return;
    }

    if (hasAlias(beanName, alias)) {
      return;
    }

    validateAlias(beanName, alias);
    super.registerAlias(beanName, alias);
    logger.debug("Registered alias '{}' for bean '{}'", alias, beanName);
  }

  /**
   * alias is validate
   */
  protected void validateAlias(String beanName, String alias) {
    // check alias circular used
    if (hasAlias(alias, beanName)) {
      throw new BeansException("Cannot register alias '" + alias +
        "' for bean '" + beanName + "': Circular reference - '" +
        beanName + "' is already defined as an alias for '" + alias + "'");
    }

    // alias is used
    if (containsBean(alias) && !beanName.equals(transformedBeanName(alias))) {
      throw new BeansException("Cannot register alias '" + alias +
        "' for bean '" + beanName + "': It's already in use for bean '" +
        transformedBeanName(alias) + "'");
    }
  }

  /**
   * check has the curtain alias
   */
  protected boolean hasAlias(String name, String alias) {
    String[] aliases = getAliases(name);
    for (String registeredAlias : aliases) {
      if (registeredAlias.equals(alias)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String[] getAliases(String name) {
    String beanName = transformedBeanName(name);
    List<String> aliases = new ArrayList<>();

    // get direct aliases
    String[] directAliases = super.getAliases(beanName);
    if (directAliases != null) {
      for (String alias : directAliases) {
        aliases.add(alias);
        // get the aliases of aliases
        String[] transitiveAliases = super.getAliases(alias);
        if (transitiveAliases != null) {
          for (String transitiveAlias : transitiveAliases) {
            if (!aliases.contains(transitiveAlias)) {
              aliases.add(transitiveAlias);
            }
          }
        }
      }
    }

    return aliases.toArray(new String[0]);
  }

  @Override
  public boolean isAlias(String name) {
    return super.isAlias(name);
  }

  @Override
  public void removeAlias(String alias) {
    super.removeAlias(alias);
    logger.debug("Removed alias '{}' from bean factory", alias);
  }

  @Override
  public ConfigurableBeanFactory getBeanFactory() {
    return this;
  }

  protected void cleanupSingletonCache(String beanName) {
    synchronized (this.singletonObjects) {
      // remove from all cache
      this.singletonObjects.remove(beanName);
      this.earlySingletonObjects.remove(beanName);
      this.singletonFactories.remove(beanName);
      // remove bean definition
      this.mergedBeanDefinitions.remove(beanName);

      // remove all alies
      String[] aliases = getAliases(beanName);
      for (String alias : aliases) {
        this.singletonObjects.remove(alias);
        this.earlySingletonObjects.remove(alias);
        this.singletonFactories.remove(alias);
        this.mergedBeanDefinitions.remove(alias);
      }
    }
  }

  @Override
  protected void beforeSingletonCreation(String beanName) {
    if (!this.singletonsCurrentlyInCreation.add(beanName)) {
      // bean is in creating, mean circular dependence
      throw new BeansException("Circular dependency detected: " + beanName);
    }
  }

  @Override
  protected void afterSingletonCreation(String beanName) {
    if (!this.singletonsCurrentlyInCreation.remove(beanName)) {
      logger.warn("Bean '{}' was not in creation, this might indicate a problem", beanName);
    }
  }

  @Override
  public <T> T createBean(Class<T> beanClass) throws BeansException {
    try {
      T instance = beanClass.getDeclaredConstructor().newInstance();
      logger.debug("Created new instance of bean class [{}]", beanClass.getName());
      return instance;
    } catch (Exception e) {
      throw new BeansException("Error creating bean with class '" + beanClass.getName() + "'", e);
    }
  }

  @Override
  public void autowireBean(Object existingBean) throws BeansException {
    logger.debug("Autowiring bean of type [{}]", existingBean.getClass().getName());
  }

  @Override
  public Object configureBean(Object existingBean, String beanName) throws BeansException {
    Object result = existingBean;

    // BeanPostProcessor before
    for (BeanPostProcessor processor : getBeanPostProcessors()) {
      Object current = processor.postProcessBeforeInitialization(result, beanName);
      if (current == null) {
        return result;
      }
      result = current;
    }

    // BeanPostProcessor after
    for (BeanPostProcessor processor : getBeanPostProcessors()) {
      Object current = processor.postProcessAfterInitialization(result, beanName);
      if (current == null) {
        return result;
      }
      result = current;
    }

    logger.debug("Configured bean [{}] of type [{}]", beanName, existingBean.getClass().getName());
    return result;
  }

  @Override
  public Object resolveDependency(Class<?> descriptor, String beanName) throws BeansException {
    logger.debug("Resolving dependency of type [{}] for bean [{}]", descriptor.getName(), beanName);
    return null;
  }

  @Override
  public int getBeanPostProcessorCount() {
    return getBeanPostProcessors().size();
  }

  protected void destroyBean(String beanName) {
    synchronized(this.singletonObjects) {
      // get all aliases
      String[] aliases = getAliases(beanName);

      // clear cache(3 level cache)
      cleanupSingletonCache(beanName);

      // clear dependencies
      this.dependentBeanMap.remove(beanName);
      this.dependenciesForBeanMap.remove(beanName);
      this.dependencyGraph.remove(beanName);

      // clear aliases dependencies
      if (aliases != null) {
        for (String alias : aliases) {
          cleanupSingletonCache(alias);
          this.dependentBeanMap.remove(alias);
          this.dependenciesForBeanMap.remove(alias);
          this.dependencyGraph.remove(alias);
        }
      }

      // remove from bean creating set
      this.beansInCreation.remove(beanName);
    }
  }

  @Override
  protected Object doGetBean(String beanName) throws BeansException {
    String canonicalName = canonicalName(beanName);
    Object bean;

    // if it is singleton
    if (isSingleton(canonicalName)) {
      // get singleton
      bean = getSingleton(canonicalName);
      if (bean == null) {
        BeanDefinition beanDefinition = getBeanDefinition(canonicalName);
        bean = createBean(canonicalName, beanDefinition);
        // singleton cache(1st cache)
        addSingleton(canonicalName, bean);
      }
    } else {
      // prototype，creating new instance
      BeanDefinition beanDefinition = getBeanDefinition(canonicalName);
      bean = createBean(canonicalName, beanDefinition);
    }

    return bean;
  }

  /**
   * initialize bean
   */
  protected Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
    // Aware
    if (bean instanceof Aware) {
      if (bean instanceof BeanFactoryAware) {
        ((BeanFactoryAware) bean).setBeanFactory(this);
      }
      if (bean instanceof BeanNameAware) {
        ((BeanNameAware) bean).setBeanName(beanName);
      }
    }

    // BeanPostProcessor before init
    Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

    // init method
    try {
      invokeInitMethods(beanName, wrappedBean, beanDefinition);
    } catch (Exception e) {
      throw new BeansException("Invocation of init method failed", e);
    }

    // BeanPostProcessor after initialize
    wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    return wrappedBean;
  }

  /**
   * bean init
   */
  protected void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
    // InitializingBean implement bean
    if (bean instanceof InitializingBean) {
      ((InitializingBean) bean).afterPropertiesSet();
    }

    // custom init-method
    String initMethodName = beanDefinition.getInitMethodName();
    if (StringUtils.hasText(initMethodName)) {
      Method initMethod = bean.getClass().getMethod(initMethodName);
      if (initMethod == null) {
        throw new BeansException("Could not find an init method named '" + initMethodName + "' on bean with name '" + beanName + "'");
      }
      initMethod.invoke(bean);
    }
  }

  /**
   * register bean implement disposable
   */
  protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
    // only singleton need to destroy
    if (!beanDefinition.isSingleton()) {
      return;
    }

    if (bean instanceof DisposableBean || StringUtils.hasText(beanDefinition.getDestroyMethodName())) {
      registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));
    }
  }

  @Override
  protected Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
    throws BeansException {
    Object result = existingBean;
    for (BeanPostProcessor processor : getBeanPostProcessors()) {
      Object current = processor.postProcessBeforeInitialization(result, beanName);
      if (current == null) {
        return result;
      }
      result = current;
    }
    return result;
  }

  @Override
  protected Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
    throws BeansException {
    Object result = existingBean;
    for (BeanPostProcessor processor : getBeanPostProcessors()) {
      Object current = processor.postProcessAfterInitialization(result, beanName);
      if (current == null) {
        return result;
      }
      result = current;
    }
    return result;
  }

}
