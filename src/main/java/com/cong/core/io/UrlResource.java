package com.cong.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cong.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UrlResource implements Resource {
  private static final Logger logger = LoggerFactory.getLogger(UrlResource.class);

  private final URL url;
  //create resource by url string
  public UrlResource(String url) throws MalformedURLException {
    Assert.notNull(url, "URL must not be null");
    this.url = new URL(url);
  }
  //create resource by URL
  public UrlResource(URL url) {
    Assert.notNull(url, "URL must not be null");
    this.url = url;
  }


  @Override
  public InputStream getInputStream() throws IOException {
    URLConnection con = this.url.openConnection();
    try {
      return con.getInputStream();
    } catch (IOException ex) {
      // if it is http, try to disconnect
      if (con instanceof HttpURLConnection) {
        ((HttpURLConnection) con).disconnect();
      }
      throw ex;
    }
  }

  @Override
  public boolean exists() {
    try {
      URLConnection con = this.url.openConnection();
      if (con instanceof HttpURLConnection) {
        HttpURLConnection httpCon = (HttpURLConnection) con;
        httpCon.setRequestMethod("HEAD");
        int code = httpCon.getResponseCode();
        httpCon.disconnect();
        return (code >= 200 && code < 300);
      }

      // no http, try to get input stream
      try (InputStream is = con.getInputStream()) {
        return true;
      }
    } catch (IOException ex) {
      logger.debug("Failed to check existence of {}: {}", this.url, ex.getMessage());
      return false;
    }
  }


  @Override
  public String getDescription() {
    return "URL [" + this.url + "]";
  }

  @Override
  public String getFilename() {
    String path = url.getPath();
    return path.substring(path.lastIndexOf('/') + 1);
  }

  @Override
  public boolean isReadable() {
    return true;
  }

  @Override
  public long lastModified() throws IOException {
    URLConnection con = this.url.openConnection();
    try {
      return con.getLastModified();
    } finally {
      if (con instanceof HttpURLConnection) {
        ((HttpURLConnection) con).disconnect();
      }
    }
  }

  public URL getURL() {
    return this.url;
  }

  public UrlResource createRelative(String relativePath) throws MalformedURLException {
    return new UrlResource(new URL(this.url, relativePath));
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof UrlResource)) {
      return false;
    }
    return this.url.equals(((UrlResource) obj).url);
  }

  @Override
  public int hashCode() {
    return this.url.hashCode();
  }
}
