package com.cong.context;

import com.cong.beans.factory.Aware;

public interface ApplicationContextAware extends Aware {
  /**
   * set application context
   */
  void setApplicationContext(ApplicationContext applicationContext);
}
