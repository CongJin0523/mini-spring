package com.cong.context.support;

import com.cong.beans.factory.BeanFactory;
import com.cong.beans.factory.config.ConfigurableListableBeanFactory;
import com.cong.context.ApplicationContext;
import com.cong.core.io.DefaultResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ApplicationContext {
  private static final Logger logger = LoggerFactory.getLogger(AbstractApplicationContext.class);

  private final long startupDate;
  private final AtomicBoolean active = new AtomicBoolean();
  private final AtomicBoolean closed = new AtomicBoolean();
  private ApplicationContext parent;
  private String id;
  private String displayName;


  public AbstractApplicationContext() {
    this(null);
  }

  public AbstractApplicationContext(ApplicationContext parent) {
    this.parent = parent;
    this.startupDate = System.currentTimeMillis();
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public long getStartupDate() {
    return this.startupDate;
  }

  @Override
  public ApplicationContext getParent() {
    return this.parent;
  }

  /**
   * refresh template
   */
  public void refresh() throws Exception {
    synchronized (this) {
      // prepare
      prepareRefresh();
      //init bean factory
      // get bean factory
      ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

      // prepare beanFactory
      prepareBeanFactory(beanFactory);

      try {
        // bean factory post processor
        postProcessBeanFactory(beanFactory);

        // invoke BeanFactoryPostProcessor
        invokeBeanFactoryPostProcessors(beanFactory);

        // regitser BeanPostProcessor
        registerBeanPostProcessors(beanFactory);

        // init message source
        initMessageSource();

        // init application even
        initApplicationEventMulticaster();

        // init other special bean
        onRefresh();

        // register listener
        registerListeners();

        finishBeanFactoryInitialization(beanFactory);

        // finish
        finishRefresh();
      } catch (Exception ex) {
        logger.error("Context refresh failed", ex);
        throw ex;
      }
    }
  }

  protected void prepareRefresh() {
    this.active.set(true);
    this.closed.set(false);
    logger.info("Refreshing " + getDisplayName());
  }

  protected abstract ConfigurableListableBeanFactory obtainFreshBeanFactory();

  protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    // set class loader
    beanFactory.setBeanClassLoader(getClassLoader());
  }

  protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
  }

  protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
  }

  protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
  }

  protected void initMessageSource() {
  }

  protected void initApplicationEventMulticaster() {
  }

  protected void onRefresh() {
  }

  protected void registerListeners() {
  }

  protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
  }

  protected void finishRefresh() {
  }

  /**
   * set id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * set display Name
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public Object getBean(String name) {
    return getBeanFactory().getBean(name);
  }

  @Override
  public <T> T getBean(String name, Class<T> requiredType) {
    return getBeanFactory().getBean(name, requiredType);
  }

  @Override
  public boolean containsBean(String name) {
    return getBeanFactory().containsBean(name);
  }

  @Override
  public boolean isSingleton(String name) {
    return getBeanFactory().isSingleton(name);
  }

  @Override
  public boolean isPrototype(String name) {
    return getBeanFactory().isPrototype(name);
  }

  @Override
  public Class<?> getType(String name) {
    return getBeanFactory().getType(name);
  }

  /**
   * get inner bean factory
   */
  protected abstract BeanFactory getBeanFactory();
}
