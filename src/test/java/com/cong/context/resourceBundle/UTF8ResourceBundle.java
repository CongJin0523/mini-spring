package com.cong.context.resourceBundle;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class UTF8ResourceBundle {
  public static ResourceBundle getBundle(String baseName, Locale locale) {
    try {
      String bundleName = baseName + "_" + locale.getLanguage() + ".properties";
      return new PropertyResourceBundle(
        new InputStreamReader(
          Objects.requireNonNull(UTF8ResourceBundle.class.getClassLoader().getResourceAsStream(bundleName)),
          StandardCharsets.UTF_8
        )
      );
    } catch (Exception e) {
      throw new RuntimeException("Could not load resource bundle: " + baseName, e);
    }
  }

  public static void main(String[] args) {
    ResourceBundle bundle = getBundle("messages", new Locale("zh")); // Load messages_fr.properties
    System.out.println(bundle.getString("greeting")); // Output: Bienvenue sur notre site!
  }
}
