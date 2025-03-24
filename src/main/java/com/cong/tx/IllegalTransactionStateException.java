package com.cong.tx;

import com.cong.beans.exception.BeansException;

public class IllegalTransactionStateException extends BeansException {
  /**
   * 使用指定的错误消息构造新的异常
   *
   * @param message 错误消息
   */
  public IllegalTransactionStateException(String message) {
    super(message);
  }

  /**
   * 使用指定的错误消息和根异常构造新的异常
   *
   * @param message 错误消息
   * @param cause 根异常
   */
  public IllegalTransactionStateException(String message, Throwable cause) {
    super(message, cause);
  }
}
