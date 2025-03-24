package com.cong.context.annotation;

public interface Condition {

  /**
   * 判断条件是否满足
   *
   * @param context 条件上下文
   * @return 如果条件满足返回true，否则返回false
   */
  boolean matches(ConditionContext context);
}