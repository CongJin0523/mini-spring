package com.cong.beans.factory.support;

/**
 * register alias for bean
 */
public interface AliaRegistry {
  void registerAlias(String name, String alias);
  void removeAlias(String alias);

  /**
   *
   * @param name alias name
   * @return
   */
  boolean isAlias(String name);

  /**
   *
   * @param name bean name
   * @return
   */
  String[] getAliases(String name);
}
