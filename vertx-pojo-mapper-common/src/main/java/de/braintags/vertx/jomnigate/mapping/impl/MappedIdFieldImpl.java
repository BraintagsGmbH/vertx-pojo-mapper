package de.braintags.vertx.jomnigate.mapping.impl;

import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IndexedIdField;
import de.braintags.vertx.jomnigate.mapping.IMappedIdField;

/**
 * Implementation of {@link IMappedIdField}
 * 
 * @author sschmitt
 * 
 */
public class MappedIdFieldImpl extends IndexedIdField implements IMappedIdField {

  private MappedField mappedField;

  /**
   * Generate a mapped ID field with the name of the mapped field as field- and column name
   * 
   * @param mappedField
   *          the mapped field that has the {@link Id} annotation
   */
  public MappedIdFieldImpl(MappedField mappedField) {
    super(mappedField.getName());
    this.mappedField = mappedField;
  }

  /**
   * Generate a mapped ID field with the name of the mapped field as field name, but a custom column name
   * 
   * @param mappedField
   *          the mapped field that has the {@link Id} annotation
   * @param columnName
   *          the name of the ID column
   */
  public MappedIdFieldImpl(MappedField mappedField, String columnName) {
    super(mappedField.getName(), columnName);
    this.mappedField = mappedField;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.MappedIdField#getField()
   */
  @Override
  public MappedField getField() {
    return mappedField;
  }

}
