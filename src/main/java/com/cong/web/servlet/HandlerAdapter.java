package com.cong.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * handler adapter interface
 * prompt handle request
 */
public interface HandlerAdapter {
  /**
   * check if handler is right
   * @param handler
   * @return
   */
  boolean supports(Object handler);

  /**
   * do the handler, return ModelAndView
   * @param request
   * @param response
   * @param handler
   * @return
   * @throws Exception
   */
  ModelAndView handle(HttpServletRequest request, HttpServletResponse response,
                      Object handler) throws Exception;
}
