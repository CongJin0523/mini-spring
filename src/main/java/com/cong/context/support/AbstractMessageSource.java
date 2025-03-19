package com.cong.context.support;

import com.cong.context.MessageSource;
import com.cong.context.MessageSourceResolvable;
import com.cong.context.NoSuchMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * abstract of messageSource interface
 * supply basic message format and cache
 */
public abstract class AbstractMessageSource implements MessageSource {
  private static final Logger logger = LoggerFactory.getLogger(AbstractMessageSource.class);

  private MessageSource parentMessageSource;
  private boolean useCodeAsDefaultMessage = false;

  @Override
  public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
    String msg = getMessageInternal(code, args, locale);
    if (msg != null) {
      return msg;
    }

    if (defaultMessage == null && useCodeAsDefaultMessage) {
      defaultMessage = code;
    }
    return defaultMessage;
  }

  @Override
  public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
    String msg = getMessageInternal(code, args, locale);
    if (msg != null) {
      return msg;
    }

    if (useCodeAsDefaultMessage) {
      return code;
    }
    throw new NoSuchMessageException(code, locale);
  }

  @Override
  public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
    String[] codes = resolvable.getCodes();
    if (codes == null) {
      codes = new String[0];
    }

    for (String code : codes) {
      String msg = getMessageInternal(code, resolvable.getArguments(), locale);
      if (msg != null) {
        return msg;
      }
    }

    if (useCodeAsDefaultMessage && codes.length > 0) {
      return codes[0];
    }

    if (resolvable.getDefaultMessage() != null) {
      return resolvable.getDefaultMessage();
    }

    if (codes.length > 0) {
      throw new NoSuchMessageException(codes[0], locale);
    }

    throw new NoSuchMessageException("No message", locale);
  }

  /**
   * inner method to get message
   * implement by child class
   * @param code
   * @param locale
   * @return
   */
  protected abstract String resolveMessage(String code, Locale locale);

  /**
   * get message and format it
   * @param code
   * @param args
   * @param locale
   * @return
   */
  protected String getMessageInternal(String code, Object[] args, Locale locale) {
    String message = resolveMessage(code, locale);
    if (message == null && parentMessageSource != null) {
      message = parentMessageSource.getMessage(code, args, null, locale);
    }
    if (message != null && args != null) {
      return formatMessage(message, args, locale);
    }
    return message;
  }

  /**
   * use messageformat
   * @param msg
   * @param args
   * @param locale
   * @return
   */
  protected String formatMessage(String msg, Object[] args, Locale locale) {
    if (msg == null || args == null || args.length == 0) {
      return msg;
    }
    msg = msg.trim();
    //format basic on locale
    MessageFormat messageFormat = new MessageFormat(msg, locale);
    return messageFormat.format(args);
  }
  public void setParentMessageSource(MessageSource parent) {
    this.parentMessageSource = parent;
  }
  public MessageSource getParentMessageSource() {
    return this.parentMessageSource;
  }

  /**
   * set using message code as default message
   * @param useCodeAsDefaultMessage
   */
  public void setUseCodeAsDefaultMessage(boolean useCodeAsDefaultMessage) {
    this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
  }

  /**
   * if using message code
   * @return
   */
  public boolean isUseCodeAsDefaultMessage() {
    return this.useCodeAsDefaultMessage;
  }
}
