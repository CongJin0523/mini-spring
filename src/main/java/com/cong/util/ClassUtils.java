package com.cong.util;

public class ClassUtils {
  /** 数组类名后缀 */
  public static final String ARRAY_SUFFIX = "[]";
  /** 内部类分隔符 */
  private static final String INNER_CLASS_SEPARATOR = "$";
  /** 包分隔符 */
  private static final String PACKAGE_SEPARATOR = ".";


  /**
   * firstly use current thread contextClassloader, 2nd use current class classloader, 3rd use system classloader
   * @return classloader
   */
  public static ClassLoader getDefaultClassLoader() {
    ClassLoader cl = null;
    try {
      cl = Thread.currentThread().getContextClassLoader();
    } catch (Throwable ex) {
      // can not get contextclassloader
    }
    if (cl == null) {
      // use classloader of current class
      cl = ClassUtils.class.getClassLoader();
      if (cl == null) {
        // use system classloader
        try {
          cl = ClassLoader.getSystemClassLoader();
        } catch (Throwable ex) {
          // can not get system class loader
        }
      }
    }
    return cl;
  }

  /**
   * get package name
   * @param clazz
   * @return
   */
  public static String getPackageName(Class<?> clazz) {
    Assert.notNull(clazz, "Class must not be null");
    return getPackageName(clazz.getName());
  }

  /**
   * get package name
   * @param fqClassName
   * @return
   */
  public static String getPackageName(String fqClassName) {
    Assert.notNull(fqClassName, "Class name must not be null");
    int lastDotIndex = fqClassName.lastIndexOf('.');
    return (lastDotIndex != -1 ? fqClassName.substring(0, lastDotIndex) : "");
  }
  /**
   * 获取类的短名称
   *
   * @param className 类名
   * @return 短名称
   */
  public static String getShortName(String className) {
    int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
    int nameEndIndex = className.indexOf(ARRAY_SUFFIX);
    if (nameEndIndex == -1) {
      nameEndIndex = className.length();
    }
    String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
    shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
    return shortName;
  }
  /**
   * 判断是否是内部类
   *
   * @param className 类名
   * @return 如果是内部类返回true，否则返回false
   */
  public static boolean isInnerClass(String className) {
    return className.contains(INNER_CLASS_SEPARATOR);
  }
}
