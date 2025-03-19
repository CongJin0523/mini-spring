package com.cong.context;

/**
 * wrapper all required info to get message
 */
public interface MessageSourceResolvable {
  String[] getCodes();
  Object[] getArguments();
  String getDefaultMessage();
}
