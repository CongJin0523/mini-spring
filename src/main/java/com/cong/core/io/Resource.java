package com.cong.core.io;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {
  //get resource input stream
  InputStream getInputStream() throws IOException;
  // resource is exists?
  boolean exists();
  // used in exception info or mark resource in log
  String getDescription();

  String getFilename();
  boolean isReadable();
  long lastModified() throws IOException;
}
