package com.cong.context.annotation;

import java.lang.annotation.*;
import java.util.concurrent.locks.Condition;

/**
 * create bean according given conditional
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Conditional {
  /**
   * 条件类数组
   * 所有条件都满足时才会创建Bean
   *
   * @return 条件类数组
   */
  Class<? extends Condition>[] value();
}
