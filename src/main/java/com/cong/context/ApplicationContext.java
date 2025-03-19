package com.cong.context;

import com.cong.beans.factory.ListableBeanFactory;
import com.cong.core.io.ResourceLoader;

/**
 * central applicationContext interface
 */
public interface ApplicationContext extends ListableBeanFactory, ResourceLoader {
  /**
   * get context identify id
   * @return
   */
  String getId();

  /**
   * get display name
   * @return
   */
  String getDisplayName();

  /**
   * get startup time
   * @return
   */
  long getStartupDate();

  /**
   * get parent context
   * @return
   */
  ApplicationContext getParent();
}
