package com.cong.context.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {
  /**
   * 要导入的类
   * 可以是配置类、ImportSelector实现类或ImportBeanDefinitionRegistrar实现类
   *
   * @return 要导入的类数组
   */
  Class<?>[] value();
}
