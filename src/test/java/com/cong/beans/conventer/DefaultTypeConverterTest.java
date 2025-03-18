package com.cong.beans.conventer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultTypeConverterTest {
  private DefaultTypeConverter converter;

  @BeforeEach
  void setUp() {
    converter = new DefaultTypeConverter();
  }

  @Test
  void testConvertPrimitiveTypes() {
    // integer
    assertEquals(123, converter.convertIfNecessary("123", int.class));
    assertEquals(123, converter.convertIfNecessary("123", Integer.class));

    // long
    assertEquals(123L, converter.convertIfNecessary("123", long.class));
    assertEquals(123L, converter.convertIfNecessary("123", Long.class));

    // double
    assertEquals(123.45, converter.convertIfNecessary("123.45", double.class));
    assertEquals(123.45, converter.convertIfNecessary("123.45", Double.class));

    // boolean
    assertTrue(converter.convertIfNecessary("true", boolean.class));
    assertTrue(converter.convertIfNecessary("true", Boolean.class));

    // char
    assertEquals('A', converter.convertIfNecessary("A", char.class));
    assertEquals('A', converter.convertIfNecessary("A", Character.class));
  }

  @Test
  void testConvertBigNumbers() {
    // BigDecimal
    BigDecimal decimal = converter.convertIfNecessary("123.45", BigDecimal.class);
    assertEquals(new BigDecimal("123.45"), decimal);

    // BigInteger
    BigInteger integer = converter.convertIfNecessary("123", BigInteger.class);
    assertEquals(new BigInteger("123"), integer);
  }

  @Test
  void testConvertDate() {
    // date
    Date date = converter.convertIfNecessary("2023-12-25 12:34:56", Date.class);
    assertNotNull(date);
  }

  @Test
  void testConvertWithNullValue() {
    // null
    assertNull(converter.convertIfNecessary(null, String.class));
    assertNull(converter.convertIfNecessary(null, Integer.class));
  }

  @Test
  void testConvertWithInvalidValue() {
    // invalid
    assertThrows(TypeMismatchException.class, () -> {
      converter.convertIfNecessary("abc", Integer.class);
    });

    assertThrows(TypeMismatchException.class, () -> {
      converter.convertIfNecessary("2023-13-45", Date.class);
    });
  }

  @Test
  void testRegisterCustomConverter() {
    // register custom converter
    converter.registerConverter(CustomType.class, CustomType::new);

    // test custom converter
    CustomType result = converter.convertIfNecessary("test", CustomType.class);
    assertEquals("test", result.getValue());
  }

  /**
   * test customType
   */
  static class CustomType {
    private final String value;

    public CustomType(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

  }
}