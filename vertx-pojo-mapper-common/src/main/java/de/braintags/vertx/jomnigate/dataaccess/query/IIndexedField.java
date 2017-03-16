package de.braintags.vertx.jomnigate.dataaccess.query;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 
 * 
 * @author sschmitt
 * 
 */
public interface IIndexedField {

  /**
   * Return the name of the field
   * 
   * @return the field name
   */
  String getFieldName();

  /**
   * Return the name of the column in the datastore, usually the same as the field name
   * 
   * @return the column name
   */
  String getColumnName();

  static IIndexedField getIndexedField(String name, Class<?> pojoClass)
      throws NoSuchFieldException, IllegalAccessException {
    Field field = pojoClass.getField(name);
    int modifiers = field.getModifiers();
    if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
        && IIndexedField.class.isAssignableFrom(field.getType())) {
      return (IIndexedField) field.get(null);
    } else
      throw new NoSuchFieldException("Field '" + name + "' for class '" + pojoClass
          + "' must be static, final, and of type " + IIndexedField.class.getName());
  }

}
