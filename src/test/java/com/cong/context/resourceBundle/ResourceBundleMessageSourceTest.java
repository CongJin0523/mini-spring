package com.cong.context.resourceBundle;

import com.cong.context.MessageSourceResolvable;
import com.cong.context.NoSuchMessageException;
import com.cong.context.support.ResourceBundleMessageSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceBundleMessageSourceTest {
  private ResourceBundleMessageSource messageSource;

  @BeforeEach
  void setUp() {
    messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages");
  }

  @Test
  void testGetMessageWithDefaultLocale() {
    String message = messageSource.getMessage("greeting", null, Locale.getDefault());
    assertNotNull(message);
    assertEquals("Hello", message);
  }

  @Test
  void testGetMessageWithChineseLocale() {
    String message = messageSource.getMessage("greeting", null, Locale.CHINESE);
    assertNotNull(message);
    assertEquals("你好", message);
  }

  @Test
  void testGetMessageWithArgs() {
    String message = messageSource.getMessage(
      "welcome",
      new Object[]{"John"},
      Locale.getDefault()
    );
    assertNotNull(message);
    assertEquals("Welcome, John!", message);
  }

  @Test
  void testGetMessageWithDefaultMessage() {
    String message = messageSource.getMessage(
      "nonexistent",
      null,
      "Default Message",
      Locale.getDefault()
    );
    assertEquals("Default Message", message);
  }

  @Test
  void testMessageNotFound() {
    assertThrows(NoSuchMessageException.class, () -> {
      messageSource.getMessage("nonexistent", null, Locale.getDefault());
    });
  }

  @Test
  void testMessageSourceResolvable() {
    MessageSourceResolvable resolvable = new MessageSourceResolvable() {
      @Override
      public String[] getCodes() {
        return new String[]{"greeting"};
      }

      @Override
      public Object[] getArguments() {
        return null;
      }

      @Override
      public String getDefaultMessage() {
        return "Default";
      }
    };

    String message = messageSource.getMessage(resolvable, Locale.getDefault());
    assertNotNull(message);
    assertEquals("Hello", message);
  }
}
