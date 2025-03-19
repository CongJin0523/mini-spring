package com.cong.context.event;

import com.cong.context.ApplicationContext;
import com.cong.context.ApplicationEvent;

/**
 * create a context refreshed event
 */
public class ContextRefreshedEvent extends ContextEvent {
  public ContextRefreshedEvent(ApplicationContext source) {
    super(source);
  }
}
