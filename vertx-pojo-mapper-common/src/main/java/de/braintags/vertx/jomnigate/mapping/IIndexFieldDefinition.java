package de.braintags.vertx.jomnigate.mapping;

import de.braintags.vertx.jomnigate.annotation.IndexType;

/**
 * Defines a single field of an {@link IIndexDefinition}
 * 
 * @author sschmitt
 *
 */
public interface IIndexFieldDefinition {

  /**
   * The name of the column to index
   * 
   * @return the name
   */
  public String getName();

  /**
   * The type of this index field
   * 
   * @return the type
   */
  public IndexType getType();
}
