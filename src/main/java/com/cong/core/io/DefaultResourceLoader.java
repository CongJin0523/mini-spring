package com.cong.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cong.util.Assert;

import java.net.MalformedURLException;
import java.net.URL;

public class DefaultResourceLoader implements ResourceLoader {
  private static final Logger logger = LoggerFactory.getLogger(DefaultResourceLoader.class);

  private ClassLoader classLoader;

  public DefaultResourceLoader() {
    this.classLoader = getClass().getClassLoader();
  }

  public DefaultResourceLoader(ClassLoader classLoader) {
    this.classLoader = classLoader != null ? classLoader : getClass().getClassLoader();
  }

  @Override
  public Resource getResource(String location) {
    Assert.notNull(location, "Location must not be null");

    if (location.startsWith(CLASSPATH_URL_PREFIX)) {
      // 类路径资源
      return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
    }

    try {
      // 尝试作为URL
      URL url = new URL(location);
      return new UrlResource(url);
    } catch (MalformedURLException ex) {
      // 作为文件系统路径
      return new FileSystemResource(location);
    }
  }

  @Override
  public ClassLoader getClassLoader() {
    return this.classLoader;
  }

  public void setClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }
}
