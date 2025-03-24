package com.cong.web.servlet;

/**
 * resolver view name to object
 */
public interface ViewResolver {
  /**
   *  resolver view name to object
   * @param viewName
   * @return return view object
   * @throws Exception
   */
  View resolveViewName(String viewName) throws Exception;

}
