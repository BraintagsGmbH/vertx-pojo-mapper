package de.braintags.vertx.jomnigate.mapping.impl;

import de.braintags.vertx.jomnigate.annotation.IndexType;
import de.braintags.vertx.jomnigate.mapping.IIndexFieldDefinition;

/**
 * Implementation of {@link IIndexFieldDefinition}
 * 
 * @author sschmitt
 *
 */
/**
 * @author sschmitt
 *
 */
public class IndexFieldDefinition implements IIndexFieldDefinition {

  private String name;
  private IndexType type;

  @Override
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public IndexType getType() {
    return type;
  }

  public void setType(final IndexType type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "IndexFieldDefinition [name=" + name + ", type=" + type + "]";
  }

}
