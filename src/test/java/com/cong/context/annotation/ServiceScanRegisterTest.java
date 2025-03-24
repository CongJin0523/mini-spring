package com.cong.context.annotation;

import com.cong.beans.factory.config.BeanDefinition;
import com.cong.beans.factory.support.DefaultListableBeanFactory;
import com.cong.core.type.AnnotationMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

public class ServiceScanRegisterTest {

  @Mock
  private DefaultListableBeanFactory registry;

  private ServiceScanRegister registrar;
  private AnnotationMetadata metadata;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    registrar = new ServiceScanRegister();
    metadata = mock(AnnotationMetadata.class);

    // 设置registry的基本行为
    when(registry.getBeanDefinitionCount()).thenReturn(0);
    doNothing().when(registry).registerBeanDefinition(anyString(), any(BeanDefinition.class));
  }

  @Test
  void shouldScanAndRegisterServices() {
    // 准备测试数据
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("basePackages", new String[]{"com.cong.test.service"});
    when(metadata.getAnnotationAttributes(eq(EnableServiceScan.class.getName())))
      .thenReturn(attributes);

    // 执行测试
    registrar.registerBeanDefinitions(metadata, registry);

    // 验证结果
    verify(registry, atLeastOnce()).registerBeanDefinition(
      eq("testService"), any(BeanDefinition.class));
  }

  @Test
  void shouldUseDefaultBasePackageWhenNotSpecified() {
    // 准备测试数据
    Map<String, Object> attributes = new HashMap<>();
    when(metadata.getAnnotationAttributes(eq(EnableServiceScan.class.getName())))
      .thenReturn(attributes);
    when(metadata.getClassName())
      .thenReturn("com.cong.test.service.TestService");

    // 执行测试
    registrar.registerBeanDefinitions(metadata, registry);

    // 验证结果
    verify(registry, atLeastOnce()).registerBeanDefinition(
      eq("testService"), any(BeanDefinition.class));
  }
}
