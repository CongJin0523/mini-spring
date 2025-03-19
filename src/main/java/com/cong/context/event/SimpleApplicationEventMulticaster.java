package com.cong.context.event;

import com.cong.context.ApplicationEvent;
import com.cong.context.ApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * simple implement of applicationEventMulticaster
 */
public class SimpleApplicationEventMulticaster implements ApplicationEventMulticaster {
  private static final Logger logger = LoggerFactory.getLogger(SimpleApplicationEventMulticaster.class);

  private final Set<ApplicationListener<?>> listeners = new LinkedHashSet<>();
  private Executor taskExecutor;

  @Override
  public void addApplicationListener(ApplicationListener<?> listener) {
    synchronized (this.listeners) {
      this.listeners.add(listener);
      logger.debug("Added application listener: {}", listener);
    }
  }

  @Override
  public void removeApplicationListener(ApplicationListener<?> listener) {
    synchronized (this.listeners) {
      this.listeners.remove(listener);
      logger.debug("Removed application listener: {}", listener);
    }
  }

  @Override
  public void removeAllListeners() {
    synchronized (this.listeners) {
      this.listeners.clear();
      logger.debug("Removed all application listeners");
    }
  }

  @Override
  public void multicastEvent(final ApplicationEvent event) {
    for (final ApplicationListener<?> listener : getApplicationListeners(event)) {
      Executor executor = getTaskExecutor();
      if (executor != null) {
        executor.execute(() -> invokeListener(listener, event));
      } else {
        invokeListener(listener, event);
      }
    }
  }


  /**
   * get task executor
   */
  protected Executor getTaskExecutor() {
    return this.taskExecutor;
  }

  /**
   * set task executor
   */
  public void setTaskExecutor(Executor taskExecutor) {
    this.taskExecutor = taskExecutor;
  }

  /**
   * get all listener for curtain task
   */
  protected Collection<ApplicationListener<?>> getApplicationListeners(ApplicationEvent event) {
    Set<ApplicationListener<?>> allListeners = new LinkedHashSet<>();
    synchronized (this.listeners) {
      for (ApplicationListener<?> listener : this.listeners) {
        if (supportsEvent(listener, event)) {
          allListeners.add(listener);
        }
      }
    }
    return allListeners;
  }

  /**
   * check listener if support curtain task
   */
  protected boolean supportsEvent(ApplicationListener<?> listener, ApplicationEvent event) {
    Class<?> listenerClass = listener.getClass();

    // check own interface
    if (supportsEventForInterfaces(listenerClass.getGenericInterfaces(), event)) {
      return true;
    }

    // check parent interface
    Class<?> superclass = listenerClass.getSuperclass();
    while (superclass != null && superclass != Object.class) {
      if (supportsEventForInterfaces(superclass.getGenericInterfaces(), event)) {
        return true;
      }
      superclass = superclass.getSuperclass();
    }

    return false;
  }

  /**
   * if interfaces support curtain task
   */
  private boolean supportsEventForInterfaces(Type[] genericInterfaces, ApplicationEvent event) {
    for (Type genericInterface : genericInterfaces) {
      if (genericInterface instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
        Type rawType = parameterizedType.getRawType();

        if (rawType == ApplicationListener.class) {
          Type[] typeArguments = parameterizedType.getActualTypeArguments();
          if (typeArguments.length == 1) {
            Type typeArgument = typeArguments[0];
            if (typeArgument instanceof Class<?>) {
              Class<?> eventClass = (Class<?>) typeArgument;
              return eventClass.isInstance(event);
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * invoke listen deal method
   *
   * @param listener
   * @param event
   */
  @SuppressWarnings("unchecked")
  protected void invokeListener(ApplicationListener listener, ApplicationEvent event) {
    try {
      listener.onApplicationEvent(event);
    } catch (Exception ex) {
      logger.error("Error invoking ApplicationListener", ex);
    }

  }
}
