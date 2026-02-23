package me.junioraww.craftingrestrictions.licenses;

import java.util.Collection;
import java.util.Set;

public class License {
  private final String id;
  private final String name;
  private final Set<String> itemStrings;

  public License(String id, String name, Collection<String> itemStrings) {
    this.id = id;
    this.name = name;
    this.itemStrings = Set.copyOf(itemStrings);
  }

  public String getId() { return id; }
  public String getName() { return name; }

  public boolean matches(Collection<String> strings) {
    if (strings == null || strings.isEmpty()) return false;
    return strings.containsAll(itemStrings);
  }
}