package com.cong.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cong.util.Assert;
import com.cong.util.ClassUtils;
import com.cong.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * load resource file which in the path of class
 */
public class ClassPathResource implements Resource {

  private static final Logger logger = LoggerFactory.getLogger(ClassPathResource.class);

  private final String path;
  private final ClassLoader classLoader;
  private final Class<?> clazz;

  public ClassPathResource(String path) {
    this(path, (ClassLoader) null);
  }
  public ClassPathResource(String path, ClassLoader classLoader) {
    Assert.notNull(path, "Path must not be null");
    this.path = StringUtils.cleanPath(path);
    this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
    this.clazz = null;
  }

  public ClassPathResource(String path, Class<?> clazz) {
    Assert.notNull(path, "Path must not be null");
    this.path = StringUtils.cleanPath(path);
    this.clazz = clazz;
    this.classLoader = null;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    InputStream is;
    if (this.clazz != null) {
      String pathToUse = this.path;
      if (!pathToUse.startsWith("/")) {
        pathToUse = "/" + pathToUse;
      }
      // load from the class path
      is = this.clazz.getResourceAsStream(pathToUse);
    } else if (this.classLoader != null) {
      //load from the root path
      is = this.classLoader.getResourceAsStream(this.path);
    } else {
      is = ClassLoader.getSystemResourceAsStream(this.path);
    }

    if (is == null) {
      throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");
    }

    logger.debug("Opened InputStream for {}", getDescription());
    return is;
  }


  @Override
  public boolean exists() {
    URL url = null;
    if (this.clazz != null) {
      url = this.clazz.getResource(this.path);
    } else if (this.classLoader != null) {
      url = this.classLoader.getResource(this.path);
    } else {
      url = ClassLoader.getSystemResource(this.path);
    }
    return url != null;
  }

  @Override
  public String getDescription() {
    StringBuilder builder = new StringBuilder("class path resource [");
    if (this.clazz != null) {
      builder.append(this.clazz.getName()).append('/');
    }
    builder.append(this.path).append(']');
    return builder.toString();
  }

  @Override
  public String getFilename() {
    return StringUtils.getFilename(this.path);
  }

  @Override
  public boolean isReadable() {
    return exists();
  }

  @Override
  public long lastModified() throws IOException {
    URL url = null;
    if (this.clazz != null) {
      url = this.clazz.getResource(this.path);
    } else if (this.classLoader != null) {
      url = this.classLoader.getResource(this.path);
    } else {
      url = ClassLoader.getSystemResource(this.path);
    }

    if (url == null) {
      throw new FileNotFoundException(getDescription() + " cannot be resolved in the file system for resolving its last-modified timestamp");
    }

    try {
      return url.openConnection().getLastModified();
    } catch (IOException ex) {
      logger.debug("Could not get last-modified timestamp for {}: {}", getDescription(), ex.getMessage());
      throw ex;
    }
  }

  public String getPath() {
    return this.path;
  }

  public ClassLoader getClassLoader() {
    return (this.clazz != null ? this.clazz.getClassLoader() : this.classLoader);
  }
}
