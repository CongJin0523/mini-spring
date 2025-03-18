package com.cong.beans.factory.config;

public class PropertyValue {
  private final String name;
  private final Object value;
  private final Class<?> type;

  /**
   * create a property
   * @param name property name
   * @param value property value
   * @param type property type
   */
  public PropertyValue(String name, Object value, Class<?> type) {
    this.name = name;
    this.value = value;
    this.type = type;
  }

  public String getName() {
    return name;
  }
  public Object getValue() {
    return value;
  }

  public Class<?> getType() {
    return type;
  }
}
