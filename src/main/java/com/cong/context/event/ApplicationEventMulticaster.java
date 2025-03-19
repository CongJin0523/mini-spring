package com.cong.context.event;

import com.cong.context.ApplicationEvent;
import com.cong.context.ApplicationListener;

/**
 * event register and publisher
 */
public interface ApplicationEventMulticaster {
  /**
   * add a listener
   * @param listener
   */
  void addApplicationListener(ApplicationListener<?> listener);

  /**
   * remove a listener
   * @param listener
   */
  void removeApplicationListener(ApplicationListener<?> listener);

  /**
   * remove all listeners
   */
  void removeAllListeners();

  /**
   * broadcast event
   * @param event
   */
  void multicastEvent(ApplicationEvent event);
}
