package com.cong.context.annotation;

import java.lang.annotation.*;

/**
 * auto scan curtain package, @Service class
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ServiceScanRegister.class)
public @interface EnableServiceScan {

  /**
   * 指定要扫描的包路径
   * 如果未指定，将使用标注此注解的类所在的包作为基础包
   *
   * @return 要扫描的包路径数组
   */
  String[] basePackages() default {};
}
