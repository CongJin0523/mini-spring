package com.cong.tx;

import com.cong.beans.exception.BeansException;

public class TransactionException extends BeansException {
  /**
   * 创建一个新的事务异常
   *
   * @param message 异常信息
   */
  public TransactionException(String message) {
    super(message);
  }

  /**
   * 创建一个新的事务异常
   *
   * @param message 异常信息
   * @param cause   异常原因
   */
  public TransactionException(String message, Throwable cause) {
    super(message, cause);

  }
}
