package com.cong.context;

import java.util.EventListener;

public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
  /**
   * deal application event
   * @param event
   */
  void onApplicationEvent(E event);
}

