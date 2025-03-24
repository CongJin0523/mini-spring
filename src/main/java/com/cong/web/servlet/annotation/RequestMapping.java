package com.cong.web.servlet.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

  String value() default "";

  RequestMethod[] method() default {};
}
