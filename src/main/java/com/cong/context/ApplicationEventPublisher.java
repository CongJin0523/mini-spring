package com.cong.context;

/**
 * application publisher
 */
public interface ApplicationEventPublisher {
  /**
   * publisher even
   */
  void publishEvent(ApplicationEvent event);
  void publishEvent(Object event);
}
