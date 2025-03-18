package com.cong.beans.conventer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DefaultTypeConverter implements TypeConverter {

  private static final Logger logger = LoggerFactory.getLogger(DefaultTypeConverter.class);

  private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /**
   * typeConverter mapping
   */
  private final Map<Class<?>, Function<String, ?>> converters = new HashMap<>();

  //user lambda
  public DefaultTypeConverter() {
    converters.put(Integer.class, Integer::valueOf);
    converters.put(int.class, Integer::parseInt);
    converters.put(Long.class, Long::valueOf);
    converters.put(long.class, Long::parseLong);
    converters.put(Double.class, Double::valueOf);
    converters.put(double.class, Double::parseDouble);
    converters.put(Float.class, Float::valueOf);
    converters.put(float.class, Float::parseFloat);
    converters.put(Boolean.class, Boolean::valueOf);
    converters.put(boolean.class, Boolean::parseBoolean);
    converters.put(Short.class, Short::valueOf);
    converters.put(short.class, Short::parseShort);
    converters.put(Byte.class, Byte::valueOf);
    converters.put(byte.class, Byte::parseByte);
    converters.put(Character.class, s -> s.charAt(0));
    converters.put(char.class, s -> s.charAt(0));

    //other common class converters
    converters.put(BigDecimal.class, BigDecimal::new);
    converters.put(BigInteger.class, BigInteger::new);
    converters.put(String.class, String::valueOf);
    converters.put(Date.class, this::parseDate);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException {
    if (value == null) {
      return null;
    }

    // if value.getclass = reuiqredType
    if (requiredType.isInstance(value)) {
      return (T) value;
    }

    // if value is Sring, try convert
    if (value instanceof String) {
      String stringValue = (String) value;
      try {
        // get converter
        Function<String, ?> converter = converters.get(requiredType);
        if (converter != null) {
          Object result = converter.apply(stringValue);
          logger.debug("Converted string value '{}' to type '{}'", stringValue, requiredType.getName());
          return (T) result;
        }
      } catch (Exception e) {
        throw new TypeMismatchException(value, requiredType, e);
      }
    }

    // not suitable converter, throw exception
    throw new TypeMismatchException(value, requiredType);
  }


  private Date parseDate(String dateStr) {
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
      return dateFormat.parse(dateStr);
    } catch (ParseException e) {
      throw new IllegalArgumentException("Failed to parse date: " + dateStr, e);
    }
  }



  //register custom converter
  public <T> void registerConverter(Class<T> type, Function<String, T> converter) {
    converters.put(type, converter);
    logger.debug("Registered converter for type '{}'", type.getName());
  }
}
