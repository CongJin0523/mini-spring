package util;

public class ClassUtils {

  private static final ClassLoader[] EMPTY_CLASS_LOADER_ARRAY = new ClassLoader[0];

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
   * is innerClass?
   * @param clazz
   * @return
   */
  public static boolean isInnerClass(Class<?> clazz) {
    return (clazz != null && clazz.isMemberClass() && !isStaticClass(clazz));
  }

  /**
   * is static class
   * @param clazz
   * @return
   */
  public static boolean isStaticClass(Class<?> clazz) {
    return (clazz != null && clazz.getModifiers() == java.lang.reflect.Modifier.STATIC);
  }
}
