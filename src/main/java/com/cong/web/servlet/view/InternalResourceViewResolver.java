package com.cong.web.servlet.view;

import com.cong.web.servlet.View;
import com.cong.web.servlet.ViewResolver;

public class InternalResourceViewResolver implements ViewResolver {

  private String prefix = "";
  private String suffix = "";
  private String contentType = "text/html;charset=UTF-8";

  @Override
  public View resolveViewName(String viewName) throws Exception {
    return new InternalResourceView(getPrefix() + viewName + getSuffix(), getContentType());
  }

  /**
   * 设置视图路径的前缀
   * @param prefix 前缀
   */
  public void setPrefix(String prefix) {
    this.prefix = (prefix != null ? prefix : "");
  }

  /**
   * 获取视图路径的前缀
   * @return 前缀
   */
  public String getPrefix() {
    return this.prefix;
  }

  /**
   * 设置视图路径的后缀
   * @param suffix 后缀
   */
  public void setSuffix(String suffix) {
    this.suffix = (suffix != null ? suffix : "");
  }

  /**
   * 获取视图路径的后缀
   * @return 后缀
   */
  public String getSuffix() {
    return this.suffix;
  }

  /**
   * 设置响应的内容类型
   * @param contentType 内容类型
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   * 获取响应的内容类型
   * @return 内容类型
   */
  public String getContentType() {
    return this.contentType;
  }
}