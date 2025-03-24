package com.cong.core.type.filter;

import com.cong.core.type.ClassMetadata;

public interface TypeFilter {
  /**
   * 判断给定的类是否匹配过滤条件
   *
   * @param metadata 类的元数据
   * @return 如果匹配返回true，否则返回false
   */
  boolean match(ClassMetadata metadata);
}
