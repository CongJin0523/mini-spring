package com.cong.beans.factory.support;


import com.cong.beans.exception.BeansException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class SimpleAliasRegistry  implements AliaRegistry{

  private static final Logger logger = LoggerFactory.getLogger(SimpleAliasRegistry.class);

  // key is alias, value is bean name
  private final Map<String, String> aliasMap = new ConcurrentHashMap<>(16);

  @Override
  public void registerAlias(String name, String alias) {
    //don't use alias
    if (alias.equals(name)) {
      removeAlias(alias);
      return;
    }

    // check circular reference
    if (hasAliasCycle(name, alias)) {
      throw new BeansException("Circular reference between alias '" + alias + "' and name '" + name + "'");
    }

    String registeredName = aliasMap.get(alias);
    if (registeredName != null) {
      if (registeredName.equals(name)) {
        // already registered, return
        return;
      }
      throw new BeansException("Cannot register alias '" + alias + "' for name '" +
        name + "': It is already registered for name '" + registeredName + "'");
    }

    aliasMap.put(alias, name);
    logger.debug("Registered alias '{}' for name '{}'", alias, name);
  }

  @Override
  public void removeAlias(String alias) {
    String name = aliasMap.remove(alias);
    if (name != null) {
      logger.debug("Removed alias '{}' for name '{}'", alias, name);
    }
  }

  @Override
  public boolean isAlias(String name) {
    return aliasMap.containsKey(name);
  }

  @Override
  public String[] getAliases(String name) {
    List<String> aliases = new ArrayList<>();
    for (Map.Entry<String, String> entry : aliasMap.entrySet()) {
      if (entry.getValue().equals(name)) {
        aliases.add(entry.getKey());
      }
    }
    return aliases.toArray(new String[0]);
  }

  /**
   *  if cycle reference alias exists
   * @param name
   * @param alias
   * @return
   */
  protected boolean hasAliasCycle(String name, String alias) {
    String registeredName = aliasMap.get(name);
    while (registeredName != null) {
      if (registeredName.equals(alias)) {
        return true;
      }
      registeredName = aliasMap.get(registeredName);
    }
    return false;
  }

  /**
   * find the canonicalName, the first name
   * @param name
   * @return
   */
  public String canonicalName(String name) {
    String canonicalName = name;
    String resolvedName;

    do {
      resolvedName = aliasMap.get(canonicalName);
      if (resolvedName != null) {
        canonicalName = resolvedName;
      }
    } while (resolvedName != null);

    return canonicalName;
  }
}
