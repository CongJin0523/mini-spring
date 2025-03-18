package com.cong.beans.conventer;

import com.cong.beans.exception.BeansException;

public class TypeMismatchException extends BeansException {
  private final Class<?> requiredType;
  private final Object value;

  public TypeMismatchException(Object value, Class<?> requiredType) {
    super("Failed to convert value '" + value + "' to type '" + requiredType.getName() + "'");
    this.value = value;
    this.requiredType = requiredType;
  }

  public TypeMismatchException(Object value, Class<?> requiredType, Throwable cause) {
    super("Failed to convert value '" + value + "' to type '" + requiredType.getName() + "'", cause);
    this.value = value;
    this.requiredType = requiredType;
  }

  public Class<?> getRequiredType() {
    return requiredType;
  }

  public Object getValue() {
    return value;
  }
}
