package util;

public class StringUtils {
  /**
   * deal // in string path
   * @param path
   * @return
   */
  public static String cleanPath(String path) {
    if (path == null) {
      return null;
    }

    // replace \\ to /
    String pathToUse = path.replace('\\', '/');

    // remove duplicate //
    while (pathToUse.contains("//")) {
      pathToUse = pathToUse.replace("//", "/");
    }

    return pathToUse;
  }

  /**
   * get filename
   * @param path
   * @return filename
   */
  public static String getFilename(String path) {
    if (path == null) {
      return null;
    }

    int separatorIndex = path.lastIndexOf('/');
    return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
  }

  public static boolean isEmpty(String str) {
    return str == null || str.isEmpty();
  }


  public static boolean hasLength(String str) {
    return str != null && !str.isEmpty();
  }

  public static boolean hasText(String str) {
    return str != null && !str.trim().isEmpty();
  }


}
