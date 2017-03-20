package de.braintags.vertx.jomnigate.mapping;

import de.braintags.vertx.jomnigate.dataaccess.query.IdField;

/**
 * Interface to combine an {@link IProperty} with an {@link IdField}
 * 
 * @author sschmitt
 * 
 */
public interface IMappedIdField extends IdField {

  /**
   * Return the underlying mapped field
   * 
   * @return the field
   */
  public IProperty getField();

}
