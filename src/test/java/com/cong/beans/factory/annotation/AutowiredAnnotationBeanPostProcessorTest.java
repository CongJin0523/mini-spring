package com.cong.beans.factory.annotation;

import com.cong.beans.exception.BeansException;
import com.cong.beans.factory.BeanFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AutowiredAnnotationBeanPostProcessorTest {
  @Mock
  private BeanFactory beanFactory;

  private AutowiredAnnotationBeanPostProcessor processor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    processor = new AutowiredAnnotationBeanPostProcessor();
    processor.setBeanFactory(beanFactory);
  }

  @Test
  void shouldAutowireField() {
    // 准备测试数据
    TestBean testBean = new TestBean();
    DependencyBean dependency = new DependencyBean();
    when(beanFactory.getBean("dependency")).thenReturn(dependency);

    // 执行测试
    processor.postProcessPropertyValues(null, testBean, "testBean");

    // 验证结果
    assertNotNull(testBean.getDependency());
    assertEquals(dependency, testBean.getDependency());
    verify(beanFactory).getBean("dependency");
  }

  @Test
  void shouldSkipNonAnnotatedField() {
    // 准备测试数据
    TestBeanWithoutAnnotation testBean = new TestBeanWithoutAnnotation();

    // 执行测试
    processor.postProcessPropertyValues(null, testBean, "testBean");

    // 验证结果
    assertNull(testBean.getDependency());
    verify(beanFactory, never()).getBean(anyString());
  }

  @Test
  void shouldHandleNonRequiredDependency() {
    // 准备测试数据
    TestBeanWithNonRequiredDependency testBean = new TestBeanWithNonRequiredDependency();
    when(beanFactory.getBean("dependency")).thenReturn(null);

    // 执行测试
    processor.postProcessPropertyValues(null, testBean, "testBean");

    // 验证结果
    assertNull(testBean.getDependency());
    verify(beanFactory).getBean("dependency");
  }

  @Test
  void shouldThrowExceptionForRequiredDependency() {
    // 准备测试数据
    TestBean testBean = new TestBean();
    when(beanFactory.getBean("dependency")).thenReturn(null);

    // 验证异常抛出
    assertThrows(BeansException.class, () -> {
      processor.postProcessPropertyValues(null, testBean, "testBean");
    });
    verify(beanFactory).getBean("dependency");
  }

  // 测试用的Bean类
  static class TestBean {
    @Autowired
    private DependencyBean dependency;

    public DependencyBean getDependency() {
      return dependency;
    }
  }

  static class TestBeanWithoutAnnotation {
    private DependencyBean dependency;

    public DependencyBean getDependency() {
      return dependency;
    }
  }

  static class TestBeanWithNonRequiredDependency {
    @Autowired(required = false)
    private DependencyBean dependency;

    public DependencyBean getDependency() {
      return dependency;
    }
  }

  static class DependencyBean {
  }
}
