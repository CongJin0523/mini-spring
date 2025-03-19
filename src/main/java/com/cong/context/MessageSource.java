package com.cong.context;

import java.util.Locale;

public interface MessageSource {
  /**
   * get message, if not find, return default message
   * @param code message code
   * @param args args array
   * @param defaultMessage defaultMessage
   * @param locale area
   * @return parsed message
   */
  String getMessage(String code, Object[] args, String defaultMessage, Locale locale);
  String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException;
  String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException;
}
