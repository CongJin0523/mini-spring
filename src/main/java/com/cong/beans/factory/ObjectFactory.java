package com.cong.beans.factory;

import com.cong.beans.exception.BeansException;

@FunctionalInterface
public interface ObjectFactory<T> {
  T getObject() throws BeansException;
}
