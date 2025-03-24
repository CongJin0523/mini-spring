package com.cong.steretype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
  /**
   * Bean的名称，默认为空
   * 如果未指定，将使用类名的首字母小写形式作为Bean名称
   *
   * @return Bean的名称
   */
  String value() default "";
}
