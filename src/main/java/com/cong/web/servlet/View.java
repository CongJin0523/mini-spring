package com.cong.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * view interface
 */
public interface View {
  /**
   * default view type
   */
  String DEFAULT_CONTENT_TYPE = "text/html;charset=UTF-8";

  /**
   * get view type
   */
  default String getContentType() {
    return DEFAULT_CONTENT_TYPE;
  }

  /**
   * render the view
   * @param model
   * @param request
   * @param response
   * @throws Exception
   */
  void render(Map<String, ?> model, HttpServletRequest request,
              HttpServletResponse response) throws Exception;

}
