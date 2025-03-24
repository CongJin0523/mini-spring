package com.cong.context.annotation;

import com.cong.beans.factory.support.BeanDefinitionRegistry;
import com.cong.core.env.Environment;
import com.cong.core.io.ResourceLoader;

public interface ConditionContext {

  /**
   * 获取Bean定义注册表
   *
   * @return Bean定义注册表
   */
  BeanDefinitionRegistry getRegistry();

  /**
   * 获取类加载器
   *
   * @return 类加载器
   */
  ClassLoader getClassLoader();

  /**
   * 获取环境配置
   *
   * @return 环境配置
   */
  Environment getEnvironment();

  /**
   * 获取资源加载器
   *
   * @return 资源加载器
   */
  ResourceLoader getResourceLoader();
}
