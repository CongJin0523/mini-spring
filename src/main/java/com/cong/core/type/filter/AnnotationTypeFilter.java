package com.cong.core.type.filter;

import com.cong.core.type.AnnotationMetadata;
import com.cong.core.type.ClassMetadata;

import java.lang.annotation.Annotation;

public class AnnotationTypeFilter implements TypeFilter {
  private final Class<? extends Annotation> annotationType;

  public AnnotationTypeFilter(Class<? extends Annotation> annotationType) {
    this.annotationType = annotationType;
  }

  @Override
  public boolean match(ClassMetadata metadata) {
    if (metadata instanceof AnnotationMetadata) {
      return ((AnnotationMetadata) metadata).hasAnnotation(annotationType.getName());
    }
    return false;
  }
}
