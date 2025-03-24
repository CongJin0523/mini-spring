package com.cong.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * define interceptor before or after handle
 */
public interface HandlerInterceptor {
  /**
   * before handle
   * @param request
   * @param response
   * @param handler
   * @return if continuing the handler chain, return true, default is true
   * @throws Exception
   */
  default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    return true;
  }

  /**
   * post handle, get before viewResolver
   * default is doing nothing
   * @param request
   * @param response
   * @param handler
   * @param modelAndView
   * @throws Exception
   */
  default void postHandle(HttpServletRequest request, HttpServletResponse response,
                          Object handler, ModelAndView modelAndView) throws Exception {
  }

  /**
   * after view resolver or exception
   * defualt is doing nothing
   * @param request
   * @param response
   * @param handler
   * @param ex
   * @throws Exception
   */
  default void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                               Object handler, Exception ex) throws Exception {
  }
}
