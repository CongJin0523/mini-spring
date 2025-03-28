package com.cong.boot.autoconfigure;

/**
 * all autoConfigurate class need to implement this interface
 */
public interface AutoConfiguration {

  /**
   * 配置方法，实现具体的自动配置逻辑
   */
  void configure();
}
