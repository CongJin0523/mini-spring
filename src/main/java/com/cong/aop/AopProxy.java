package com.cong.aop;

/**
 * Method about get proxy object
 */
public interface AopProxy {
  /**
   * get proxy object
   * @return
   */
  Object getProxy();
  /**
   * get proxy object use curtain classloader
   */
  Object getProxy(ClassLoader classLoader);
}
