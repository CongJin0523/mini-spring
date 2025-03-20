package com.cong.aop;

/**
 * get target object
 */
public class TargetSource {
  private final Object target;
  public TargetSource(Object target) {
    this.target = target;
  }
  public Class<?> getTargetClass() {
    return target.getClass();
  }

  public Object getTarget() {
    return target;
  }
}
