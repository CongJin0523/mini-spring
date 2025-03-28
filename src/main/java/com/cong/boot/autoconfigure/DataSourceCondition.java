package com.cong.boot.autoconfigure;

import com.cong.context.annotation.Condition;
import com.cong.context.annotation.ConditionContext;

public class DataSourceCondition implements Condition {
  @Override
  public boolean matches(ConditionContext context) {
    String driverClassName = context.getEnvironment()
      .getProperty("spring.datasource.driver-class-name");
    return driverClassName != null && !driverClassName.isEmpty();
  }
}
