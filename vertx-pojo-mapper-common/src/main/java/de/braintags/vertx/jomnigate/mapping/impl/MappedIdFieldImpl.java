package de.braintags.vertx.jomnigate.mapping.impl;

import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IndexedIdField;
import de.braintags.vertx.jomnigate.mapping.IMappedIdField;
import de.braintags.vertx.jomnigate.mapping.IProperty;

/**
 * Implementation of {@link IMappedIdField}
 * 
 * @author sschmitt
 * 
 */
public class MappedIdFieldImpl extends IndexedIdField implements IMappedIdField {

  private IProperty mappedField;

  /**
   * Generate a mapped ID field with the name of the mapped field as field- and column name
   * 
   * @param mappedField
   *          the mapped field that has the {@link Id} annotation
   */
  public MappedIdFieldImpl(IProperty mappedField) {
    super(mappedField.getName());
    this.mappedField = mappedField;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.MappedIdField#getField()
   */
  @Override
  public IProperty getField() {
    return mappedField;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((mappedField == null) ? 0 : mappedField.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj))
      return false;
    MappedIdFieldImpl other = (MappedIdFieldImpl) obj;
    if (mappedField == null) {
      if (other.mappedField != null)
        return false;
    } else if (!mappedField.equals(other.mappedField))
      return false;
    return true;
  }

}
