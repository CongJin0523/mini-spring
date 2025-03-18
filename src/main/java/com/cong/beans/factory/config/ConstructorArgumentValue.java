package com.cong.beans.factory.config;

/**
 * wrapper class of constructor value
 */
public class ConstructorArgumentValue {
  private final Object value;
  private final Class<?> type;
  private final String name;


  public ConstructorArgumentValue(Object value, Class<?> type, String name) {
    this.value = value;
    this.type = type;
    this.name = name;
  }

  public Object getValue() {
    return value;
  }

  public Class<?> getType() {
    return type;
  }

  public String getName() {
    return name;
  }
}
