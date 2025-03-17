package com.cong.beans.exception;

/**
 * Base Exception to Beans
 */
public class BeansException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /**
   * create an empty BeanException
   */
  public BeansException() {
    super();
  }

  /**
   * create a BeanException with message
   * @param message
   */
  public BeansException(String message) {
    super(message);
  }

  public BeansException(String message, Throwable cause) {
    super(message, cause);
  }

  public BeansException(Throwable cause) {
    super(cause);
  }
}
