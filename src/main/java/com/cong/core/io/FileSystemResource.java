package com.cong.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cong.util.Assert;
import com.cong.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemResource implements Resource {
  private static final Logger logger = LoggerFactory.getLogger(FileSystemResource.class);

  private final String path;
  private final File file;

  public FileSystemResource(String path) {
    Assert.notNull(path, "Path must not be null");
    this.path = StringUtils.cleanPath(path);
    this.file = new File(this.path);
  }
  public FileSystemResource(File file) {
    Assert.notNull(file, "File must not be null");
    this.path = StringUtils.cleanPath(file.getPath());
    this.file = file;
  }
  @Override
  public InputStream getInputStream() throws IOException {
    try {
      InputStream is = Files.newInputStream(this.file.toPath());
      logger.debug("Opened InputStream for {}", getDescription());
      return is;
    } catch (IOException ex) {
      throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");
    }
  }

  @Override
  public boolean exists() {
    return this.file.exists();
  }

  @Override
  public String getDescription() {
    return "file [" + this.path + "]";
  }

  @Override
  public String getFilename() {
    return this.file.getName();
  }

  @Override
  public boolean isReadable() {
    return this.file.canRead();
  }

  @Override
  public long lastModified() throws IOException {
    long lastModified = this.file.lastModified();
    if (lastModified == 0L && !this.file.exists()) {
      throw new FileNotFoundException(getDescription() +
        " cannot be resolved in the file system for resolving its last-modified timestamp");
    }
    return lastModified;
  }

  public String getAbsolutePath() {
    return this.file.getAbsolutePath();
  }

  public File getFile() {
    return this.file;
  }

  public FileSystemResource createRelative(String relativePath) {
    String pathToUse = StringUtils.cleanPath(this.path);
    Path parent = Paths.get(pathToUse).getParent();
    if (parent != null) {
      return new FileSystemResource(parent.resolve(relativePath).toString());
    } else {
      return new FileSystemResource(relativePath);
    }
  }
}
