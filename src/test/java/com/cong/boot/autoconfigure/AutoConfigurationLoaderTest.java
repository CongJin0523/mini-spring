package com.cong.boot.autoconfigure;

import com.cong.beans.factory.support.DefaultListableBeanFactory;
import com.cong.core.env.Environment;
import com.cong.core.io.DefaultResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AutoConfigurationLoaderTest {
  private AutoConfigurationLoader loader;
  private DefaultListableBeanFactory beanFactory;
  private DefaultResourceLoader resourceLoader;
  private Environment environment;

  @BeforeEach
  void setUp() {
    beanFactory = mock(DefaultListableBeanFactory.class);
    resourceLoader = new DefaultResourceLoader();
    environment = mock(Environment.class);
    when(environment.getProperty("spring.datasource.driver-class-name")).thenReturn("org.h2.Driver");
    when(environment.getProperty("spring.datasource.url")).thenReturn("jdbc:h2:mem:testdb");
    when(environment.getProperty("spring.datasource.username")).thenReturn("sa");
    when(environment.getProperty("spring.datasource.password")).thenReturn("");

    loader = new AutoConfigurationLoader(resourceLoader, beanFactory);
  }

  @Test
  void shouldLoadAutoConfigurations() {
    List<Class<?>> configurations = loader.loadAutoConfigurations();

    assertNotNull(configurations);
    assertTrue(configurations.size() > 0);
    assertTrue(configurations.stream()
      .allMatch(clazz -> AutoConfiguration.class.isAssignableFrom(clazz)));
  }

  @Test
  void shouldProcessAutoConfigurations() {
    List<Class<?>> configurations = List.of(TestAutoConfiguration.class);

    assertDoesNotThrow(() -> loader.processAutoConfigurations(configurations));
  }

  @Test
  void shouldHandleEmptyConfigurations() {
    List<Class<?>> configurations = List.of();

    assertDoesNotThrow(() -> loader.processAutoConfigurations(configurations));
  }

  @Test
  void shouldHandleInvalidConfigurations() {
    class InvalidConfiguration {}
    List<Class<?>> configurations = List.of(InvalidConfiguration.class);

    assertThrows(RuntimeException.class,
      () -> loader.processAutoConfigurations(configurations));
  }

  static class TestAutoConfiguration implements AutoConfiguration {
    @Override
    public void configure() {
      // 空实现用于测试
    }
  }
}
