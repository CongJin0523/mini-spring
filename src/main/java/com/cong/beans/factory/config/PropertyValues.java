package com.cong.beans.factory.config;

import java.util.ArrayList;
import java.util.List;

/**
 * the set of property
 */
public class PropertyValues {
  private final List<PropertyValue> propertyValueList = new ArrayList<>();

  public void addPropertyValue(PropertyValue propertyValue) {
    // 检查是否已存在同名属性
    for (int i = 0; i < propertyValueList.size(); i++) {
      PropertyValue currentValue = propertyValueList.get(i);
      if (currentValue.getName().equals(propertyValue.getName())) {
        // 如果存在，替换它
        propertyValueList.set(i, propertyValue);
        return;
      }
    }
    // 如果不存在，添加到列表
    propertyValueList.add(propertyValue);
  }

  public PropertyValue[] getPropertyValues() {
    return this.propertyValueList.toArray(new PropertyValue[0]);
  }

  public PropertyValue getPropertyValue(String propertyName) {
    for (PropertyValue propertyValue : this.propertyValueList) {
      if (propertyValue.getName().equals(propertyName)) {
        return propertyValue;
      }
    }
    return null;
  }
}
