package com.cong.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * handle chain
 * including interceptor and handle chain
 */
public class HandlerExecutionChain {
  private final Object handler;
  private final List<HandlerInterceptor> interceptors = new ArrayList<>();
  private int interceptorIndex = -1;
  private boolean afterCompletionCalled = false;

  public HandlerExecutionChain(Object handler) {
    this.handler = handler;
  }

  public Object getHandler() {
    return this.handler;
  }

  public void addInterceptor(HandlerInterceptor interceptor) {
    this.interceptors.add(interceptor);
  }

  public boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
    if (interceptors.isEmpty()) {
      return true;
    }

    for (int i = 0; i < interceptors.size(); i++) {
      HandlerInterceptor interceptor = interceptors.get(i);
      boolean result;
      try {
        result = interceptor.preHandle(request, response, this.handler);
      } catch (Exception e) {
        interceptorIndex = i;
        triggerAfterCompletion(request, response, e);
        throw e;
      }
      if (!result) {
        interceptorIndex = i;
        triggerAfterCompletion(request, response, null);
        return false;
      }
      interceptorIndex = i;
    }
    return true;
  }

  /**
   * run all post handle
   * @param request
   * @param response
   * @param mv
   * @throws Exception
   */
  public void applyPostHandle(HttpServletRequest request, HttpServletResponse response,
                              ModelAndView mv) throws Exception {
    if (interceptors.isEmpty()) {
      return;
    }
    for (int i = interceptors.size() - 1; i >= 0; i--) {
      HandlerInterceptor interceptor = interceptors.get(i);
      interceptor.postHandle(request, response, this.handler, mv);
    }
  }

  /**
   * do all afterCompletion
    * @param request
   * @param response
   * @param ex
   * @throws Exception
   */
  public void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, Exception ex) throws Exception {
    if (interceptors.isEmpty() || afterCompletionCalled) {
      return;
    }
    afterCompletionCalled = true;
    int endIndex = interceptorIndex == -1 ? interceptors.size() - 1 : interceptorIndex;
    for (int i = endIndex; i >= 0; i--) {
      HandlerInterceptor interceptor = interceptors.get(i);
      try {
        interceptor.afterCompletion(request, response, this.handler, ex);
      } catch (Throwable throwable) {
        System.err.println("HandlerInterceptor.afterCompletion threw exception: " + throwable);
      }
    }
  }
}
