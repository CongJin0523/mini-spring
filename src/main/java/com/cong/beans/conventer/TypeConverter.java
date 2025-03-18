package com.cong.beans.conventer;

/**
 * change A type to another type
 */
public interface TypeConverter {
  /**
   * change object(value) to requiredType
   * @param value
   * @param requiredType
   * @return
   * @param <T>
   * @throws TypeMismatchException
   */
  <T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException;
}
