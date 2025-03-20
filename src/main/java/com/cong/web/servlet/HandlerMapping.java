package com.cong.web.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * handler mapping
 * matching handler and request
 * to get handler chain
 */
public interface HandlerMapping {
  HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
