package de.braintags.vertx.jomnigate.mapping;

import java.util.List;

/**
 * Defines an index for a mapper of a datastore
 * 
 * @author sschmitt
 *
 */
public interface IIndexDefinition {

  /**
   * The name of the index
   * 
   * @return the name
   */
  public String getName();

  /**
   * The fields for which this index should be created
   * 
   * @return the fields
   */
  public List<IIndexFieldDefinition> getFields();

  /**
   * The options for the index, if any
   * 
   * @return the options
   */
  public List<IndexOption> getIndexOptions();

  /**
   * Return a unique identifier to prevent potentially forbidden duplicate indices for the same configuration. The
   * identifier must only be unique for the table of the index, not for the whole database
   * 
   * @return a unique identifier for this index
   */
  public String getIdentifier();
}
