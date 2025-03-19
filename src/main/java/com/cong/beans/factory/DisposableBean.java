package com.cong.beans.factory;

/**
 * how to destroy bean
 */
public interface DisposableBean {
  void destroy() throws Exception;
}
