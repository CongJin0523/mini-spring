package com.cong.context.event;

import com.cong.context.ApplicationEvent;
import com.cong.context.ApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * application Listener Adapter
 * changing normal method to
 */
public class ApplicationListenerAdapter implements ApplicationListener<ApplicationEvent> {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationListenerAdapter.class);

  private final Object target;
  private final Method method;
  private final Class<? extends ApplicationEvent> eventType;

  public ApplicationListenerAdapter(Object target, Method method, Class<? extends ApplicationEvent> eventType) {
    this.target = target;
    this.method = method;
    this.eventType = eventType;
    this.method.setAccessible(true);
  }

  @Override
  public void onApplicationEvent(ApplicationEvent event) {
    if (eventType.isInstance(event)) {
      try {
        method.invoke(target, event);
      } catch (Exception ex) {
        logger.error("Failed to invoke event listener method: " + method, ex);
      }
    }
  }

  public Class<? extends ApplicationEvent> getEventType() {
    return this.eventType;
  }

  public Object getTarget() {
    return this.target;
  }

  public Method getMethod() {
    return this.method;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof ApplicationListenerAdapter)) {
      return false;
    }
    ApplicationListenerAdapter otherAdapter = (ApplicationListenerAdapter) other;
    return (this.target.equals(otherAdapter.target) && this.method.equals(otherAdapter.method));
  }

  @Override
  public int hashCode() {
    return this.target.hashCode() * 31 + this.method.hashCode();
  }

  @Override
  public String toString() {
    return "ApplicationListenerAdapter: target = [" + this.target + "], method = [" + this.method + "]";
  }
}
