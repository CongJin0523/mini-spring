package com.cong.context.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * implement MessageSource Based on ResourceBundle
 */
public class ResourceBundleMessageSource extends AbstractMessageSource {
  private static final Logger logger = LoggerFactory.getLogger(ResourceBundleMessageSource.class);

  private String basename;
  private ClassLoader bundleClassLoader;
  private final ConcurrentMap<String, Map<Locale, ResourceBundle>> cachedResourceBundles =
    new ConcurrentHashMap<>();

  public void setBasename(String basename) {
    this.basename = basename;
  }

  public void setBundleClassLoader(ClassLoader classLoader) {
    this.bundleClassLoader = classLoader;
  }

  @Override
  protected String resolveMessage(String code, Locale locale) {
    ResourceBundle bundle = getResourceBundle(locale);
    if (bundle != null) {
      try {
        return bundle.getString(code);
      } catch (MissingResourceException ex) {
        logger.debug("No message found with code '{}' in bundle '{}'", code, bundle.getBaseBundleName());
      }
    }
    return null;
  }

  /**
   * get ResourceBundle, get form cache firstly
   */
  protected ResourceBundle getResourceBundle(Locale locale) {
    Map<Locale, ResourceBundle> bundleMap = this.cachedResourceBundles.get(this.basename);
    if (bundleMap != null) {
      ResourceBundle bundle = bundleMap.get(locale);
      if (bundle != null) {
        return bundle;
      }
    }

    try {
      ResourceBundle bundle = getBundle(locale);
      if (bundle != null) {
        Map<Locale, ResourceBundle> existing = this.cachedResourceBundles.putIfAbsent(
          this.basename, new ConcurrentHashMap<>());
        Map<Locale, ResourceBundle> map = existing != null ? existing :
          this.cachedResourceBundles.get(this.basename);
        map.put(locale, bundle);
        return bundle;
      }
    } catch (MissingResourceException ex) {
      logger.debug("No bundle found for basename '{}'", this.basename);
    }

    return null;
  }

  /**
   * loading ResourceBundle
   */
  protected ResourceBundle getBundle(Locale locale) {
    ClassLoader classLoader = this.bundleClassLoader;
    if (classLoader == null) {
      classLoader = Thread.currentThread().getContextClassLoader();
    }

    // if it has default, using default first
    if (locale.equals(Locale.getDefault())) {
      try {
        return ResourceBundle.getBundle(this.basename, Locale.ROOT, classLoader);
      } catch (MissingResourceException ex) {
        logger.debug("No default bundle found for basename '{}', falling back to system locale", this.basename);
      }
    }

    return ResourceBundle.getBundle(this.basename, locale, classLoader);
  }

  /**
   * clear cache
   */
  public void clearCache() {
    logger.debug("Clearing ResourceBundle cache");
    this.cachedResourceBundles.clear();
  }
}
