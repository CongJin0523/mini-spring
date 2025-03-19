package com.cong.beans.factory;

/**
 * run afterPropertiesSet() after setting properties
 */
public interface InitializingBean {
  void afterPropertiesSet() throws Exception;
}
