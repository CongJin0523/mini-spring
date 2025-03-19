package com.cong.context;

import com.cong.beans.exception.BeansException;

import java.util.Locale;

public class NoSuchMessageException extends BeansException {
  private static final long serialVersionUID = 1L;

  public NoSuchMessageException(String code) {
    super("No message found under code '" + code + "'");
  }

  public NoSuchMessageException(String code, Locale locale) {
    super("No message found under code '" + code + "' for locale '" + locale + "'");
  }
}
