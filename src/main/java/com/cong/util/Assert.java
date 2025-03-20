package com.cong.util;

/**
 * use to check param
 */
public class Assert {
  /**
   * object is not null
   * @param object
   * @param message
   */
  public static void notNull(Object object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * string is not null
   * @param text
   * @param message
   */
  public static void hasText(String text, String message) {
    if (text == null || text.trim().isEmpty()) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * assert expression is right
    * @param expression
   * @param message
   */
  public static void isTrue(boolean expression, String message) {
    if (!expression) {
      throw new IllegalArgumentException(message);
    }
  }
}
