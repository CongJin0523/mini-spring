package com.cong.web.servlet.handler;

import com.cong.beans.factory.InitializingBean;
import com.cong.context.ApplicationContext;
import com.cong.context.ApplicationContextAware;
import com.cong.web.servlet.HandlerExecutionChain;
import com.cong.web.servlet.HandlerMapping;
import com.cong.web.servlet.annotation.RequestMapping;
import com.cong.web.servlet.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * handle mapping basic on @RequestMapping annotation
 */
public class RequestMappingHandlerMapping implements HandlerMapping, ApplicationContextAware, InitializingBean {
  private ApplicationContext applicationContext;
  /**
   * save path and handler mapping
   * key: path
   * value: handler method info
   */
  private final Map<String, MappingRegistry.MappingRegistration> mappingLookup = new HashMap<>();
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }


  /**
   * bean initilaizaingBean
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    System.out.println("Strating initial RequestMappingHandlerMapping...");
    String[] beanNames = applicationContext.getBeanDefinitionNames();
    System.out.println("BeanNames is " + String.join(", ", beanNames));

    for (String beanName : beanNames) {
      Object bean = applicationContext.getBean(beanName);
      Class<?> beanClass   = bean.getClass();
      System.out.println("Dealing Bean: " + beanName + ", Type: " + beanClass.getName() );


      //get the annotation of @RequestMapping class
      // return value
      RequestMapping typeMapping = beanClass.getAnnotation(RequestMapping.class);
      String typePath = typeMapping != null ? typeMapping.value() : "";
      RequestMethod[] typeMethods = typeMapping != null ? typeMapping.method() : new RequestMethod[0];
      System.out.println("Type level mapping path: " + typePath + ", Methods: " );

      if (typeMapping != null) {
        Method[] methods = beanClass.getDeclaredMethods();
        boolean hasMethodMapping = false;
        for (Method method : methods) {
          if (method.isAnnotationPresent(RequestMapping.class)) {
            hasMethodMapping = true;
            break;
          }
        }

        if (!hasMethodMapping) {
          try {
            Method handleRequestMethod = beanClass.getMethod("handleRequest");
            System.out.println("found default method: handleRequest");
            registerHandlerMethod(typePath, bean, handleRequestMethod, typeMethods);
          } catch (NoSuchMethodException e) {
            System.out.println("no default method found: handleRequest");
          }
        }
      }

      //scan method with @RequestMapping
      Method[] methods = beanClass.getDeclaredMethods();
      for (Method method : methods) {
        RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
        if (methodMapping != null) {
          String methodPath = methodMapping.value();
          String path = combinePath(typePath, methodPath);
          System.out.println("Found method level mapping: " + method.getName() + " -> " + path);

          RequestMethod[] methodMethods = methodMapping.method();
          RequestMethod[] combinedMethods = methodMethods.length > 0 ? methodMethods : typeMethods;

          registerHandlerMethod(path, bean, method, combinedMethods);
        }
      }
    }
    System.out.println("handler mapping initialized, registered mapping: " + mappingLookup.keySet());
  }

  @Override
  public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
    String lookupPath = request.getRequestURI();
    String method = request.getMethod();
    System.out.println("lookupPath: " + lookupPath + ", method: " + method);

    MappingRegistry.MappingRegistration registration = mappingLookup.get(lookupPath);
    if (registration != null) {
      System.out.println("Found mapping: " + registration.method.getName());
      if (registration.methods.length == 0 || isMethodMatch(method, registration.methods)) {
        System.out.println("Request method matched successfully");
        return new HandlerExecutionChain(new HandlerMethod(registration.handler, registration.method));
      } else  {
        System.out.println("Request method not matched");
      }
    } else {
      System.out.println("No mapping found for " + lookupPath);
    }
    return null;
  }
  private String combinePath(String typePath, String methodPath) {
    if (typePath.endsWith("/")) {
      typePath = typePath.substring(0, typePath.length() - 1);
    }
    if (!methodPath.startsWith("/")) {
      methodPath = "/" + methodPath;
    }
    return typePath + methodPath;
  }

  private boolean isMethodMatch(String requestMethod, RequestMethod[] methods) {
    if (methods.length == 0) {
      return true;
    }
    for (RequestMethod method : methods) {
      if (method.name().equals(requestMethod)) {
        return true;
      }
    }
    return false;
  }

  private void registerHandlerMethod(String path, Object handler, Method method, RequestMethod[] methods) {
    System.out.println("Register HandlerMethod: " + path + " -> " + method.getName());
    MappingRegistry.MappingRegistration registration = new MappingRegistry.MappingRegistration();
    registration.handler = handler;
    registration.method = method;
    registration.methods = methods;

    mappingLookup.put(path, registration);
  }


  /**
   * 映射注册信息
   */
  private static class MappingRegistry {
    static class MappingRegistration {
      Object handler;
      Method method;
      RequestMethod[] methods = new RequestMethod[0]; // 初始化为空数组
    }
  }

  public static class HandlerMethod {
    private final Object bean;
    private final Method method;

    public HandlerMethod(Object bean, Method method) {
      this.bean = bean;
      this.method = method;
    }

    public Object getBean() {
      return bean;
    }

    public Method getMethod() {
      return method;
    }
  }
}
