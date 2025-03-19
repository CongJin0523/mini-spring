package com.cong.beans.factory;

/**
 * bean can know it own name in contain
 */
public interface BeanNameAware extends Aware {
  void setBeanName(String name);
}
