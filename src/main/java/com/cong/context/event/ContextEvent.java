package com.cong.context.event;

import com.cong.context.ApplicationContext;
import com.cong.context.ApplicationEvent;

/**
 * application context event
 * all event relating context should extend this
 */
public abstract class ContextEvent extends ApplicationEvent {
  public ContextEvent(ApplicationContext source) {
    super(source);
  }
  public final ApplicationContext getApplicationContext() {
    return (ApplicationContext) getSource();
  }
}
