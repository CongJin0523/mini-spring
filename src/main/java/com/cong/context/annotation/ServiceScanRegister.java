package com.cong.context.annotation;

import com.cong.beans.factory.support.BeanDefinitionRegistry;
import com.cong.core.type.AnnotationMetadata;
import com.cong.core.type.filter.AnnotationTypeFilter;
import com.cong.util.ClassUtils;
import com.cong.steretype.Service;

import java.util.Map;

public class ServiceScanRegister implements ImportBeanDefinitionRegister{
  @Override
  public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
    Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableServiceScan.class.getName());
    String[] basePackages = null;

    if (attributes != null) {
      basePackages = (String[]) attributes.get("basePackages");
    }

    if (basePackages == null || basePackages.length == 0) {
      String basePackage = ClassUtils.getPackageName(metadata.getClassName());
      basePackages = new String[]{basePackage};
    }

    ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
    // 添加@Service注解过滤器
    scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));
    scanner.scan(basePackages);
  }
}
