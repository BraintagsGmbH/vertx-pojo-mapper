package de.braintags.vertx.jomnigate.dataaccess.query.impl;

import de.braintags.vertx.jomnigate.dataaccess.query.IdField;

/**
 * Implementation of {@link IdField}
 * 
 * @author sschmitt
 * 
 */
public class IndexedIdField extends IndexedField implements IdField {

  public IndexedIdField(String name) {
    super(name);
  }

  public IndexedIdField(String fieldName, String columnName) {
    super(fieldName, columnName);
  }
}