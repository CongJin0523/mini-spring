package com.cong.context.event;

import com.cong.context.ApplicationContext;
import com.cong.context.ApplicationEvent;

/**
 * create a close event
 */
public class ContextClosedEvent extends ContextEvent {
  public ContextClosedEvent(ApplicationContext source) {
    super(source);
  }
}
