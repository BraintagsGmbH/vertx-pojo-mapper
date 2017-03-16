package de.braintags.vertx.jomnigate.dataaccess.query.impl;

import de.braintags.vertx.jomnigate.dataaccess.query.IIndexedField;

/**
 * 
 * 
 * @author sschmitt
 * 
 */
public class IndexedField implements IIndexedField {

  private String fieldName;
  private String columnName;

  public IndexedField(String name) {
    this(name, name);
  }

  public IndexedField(String fieldName, String columnName) {
    this.fieldName = fieldName;
    this.columnName = columnName;
  }

  @Override
  public String getFieldName() {
    return fieldName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IIndexedField#getColumnName()
   */
  @Override
  public String getColumnName() {
    return columnName;
  }
}
