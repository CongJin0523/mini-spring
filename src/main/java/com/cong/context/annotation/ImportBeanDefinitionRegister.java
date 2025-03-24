package com.cong.context.annotation;

import com.cong.beans.factory.support.BeanDefinitionRegistry;
import com.cong.core.type.AnnotationMetadata;

/**
 * used to register extra Bean definition
 */
public interface ImportBeanDefinitionRegister {

  /**
   * 根据导入的@Configuration类的注解信息向容器注册Bean定义
   *
   * @param importingClassMetadata 导入类的注解元数据
   * @param registry Bean定义注册表
   */
  void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                               BeanDefinitionRegistry registry);
}

