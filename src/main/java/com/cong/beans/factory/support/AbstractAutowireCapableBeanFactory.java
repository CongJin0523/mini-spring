package com.cong.beans.factory.support;

import com.cong.beans.exception.BeansException;
import com.cong.beans.factory.DisposableBean;
import com.cong.beans.factory.PropertyValues;
import com.cong.beans.factory.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {
  private static final Logger logger = LoggerFactory.getLogger(AbstractAutowireCapableBeanFactory.class);

  private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
  private final Map<String, DisposableBean> disposableBeans = new ConcurrentHashMap<>();

  /**
   * creating bean
   * @param beanDefinition
   * @return bean
   * @throws BeansException
   */
  protected Object createBeanInstance(BeanDefinition beanDefinition) throws BeansException {
    Class<?> beanClass = beanDefinition.getBeanClass();
    if (beanClass == null) {
      throw new BeansException("Bean class is not set for bean definition");
    }

    try {
      if (beanDefinition.hasConstructorArgumentValues()) {
        return autowireConstructor(beanDefinition);
      }
      return beanClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new BeansException("Error creating bean instance for " + beanClass, e);
    }
  }

  protected Object autowireConstructor(BeanDefinition beanDefinition) throws BeansException {
    Class<?> beanClass = beanDefinition.getBeanClass();
    List<ConstructorArgumentValue> argumentValues = beanDefinition.getConstructorArgumentValues();

    try {
      // get all constructors method
      Constructor<?>[] constructors = beanClass.getConstructors();
      for (Constructor<?> constructor : constructors) {
        if (constructor.getParameterCount() == argumentValues.size()) {
          Class<?>[] paramTypes = constructor.getParameterTypes();
          Object[] args = new Object[argumentValues.size()];

          // prepare the argument used in constructor
          for (int i = 0; i < argumentValues.size(); i++) {
            ConstructorArgumentValue argumentValue = argumentValues.get(i);
            Object value = argumentValue.getValue();
            Class<?> requiredType = paramTypes[i];

            if (value instanceof String && requiredType != String.class) {
              // if the value is String, but type is not, get bean from cache
              String refBeanName = (String) value;
              // firstly to get from cache
              if (this instanceof DefaultListableBeanFactory) {
                value = ((DefaultListableBeanFactory) this).getSingleton(refBeanName, true);
                if (value == null) {
                  value = getBean(refBeanName);
                }
              } else {
                value = getBean(refBeanName);
              }
            }
            args[i] = value;
          }

          return constructor.newInstance(args);
        }
      }
      throw new BeansException("Could not find matching constructor for " + beanClass);
    } catch (Exception e) {
      throw new BeansException("Error autowiring constructor for " + beanClass, e);
    }
  }

  /**
   * injecting dependence(setting by setter, property) to bean
   * @param beanName
   * @param bean
   * @param beanDefinition
   * @throws BeansException
   */
  protected void populateBean(String beanName, Object bean, BeanDefinition beanDefinition) throws BeansException {
    PropertyValues propertyValues = beanDefinition.getPropertyValues();
    if (propertyValues != null) {
      for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
        String propertyName = propertyValue.getName();
        Object value = propertyValue.getValue();
        Class<?> type = propertyValue.getType();

        try {
          // if the value is String, but type is not, get bean from cache
          if (value instanceof String && type != String.class) {
            String refBeanName = (String) value;
            // firstly get from cache
            if (this instanceof DefaultListableBeanFactory) {
              value = ((DefaultListableBeanFactory) this).getSingleton(refBeanName, true);
              if (value == null) {
                value = getBean(refBeanName);
              }
            } else {
              value = getBean(refBeanName);
            }
          }

          String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
          Method setter = bean.getClass().getMethod(methodName, type);
          setter.setAccessible(true);
          setter.invoke(bean, value);
        } catch (Exception e) {
          throw new BeansException("Error setting property '" + propertyName + "' for bean '" + beanName + "'", e);
        }
      }
    }
  }

  protected void registerDisposableBean(String beanName, DisposableBean bean) {
    disposableBeans.put(beanName, bean);
  }

  public void destroySingletons() {
    synchronized (this.disposableBeans) {
      for (Map.Entry<String, DisposableBean> entry : disposableBeans.entrySet()) {
        try {
          entry.getValue().destroy();
          logger.debug("Invoked destroy-method of bean '{}'", entry.getKey());
        } catch (Exception e) {
          logger.error("Error destroying bean '{}'", entry.getKey(), e);
        }
      }
      disposableBeans.clear();
    }
  }


}
