package de.braintags.vertx.jomnigate.dataaccess.query;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IndexedField;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IProperty;

/**
 * Marks a field as indexed, and thus searchable. Also contains the field and column name to prevent the need to
 * reference fields by String.
 * 
 * @author sschmitt
 * 
 */
public interface IIndexedField {

  /**
   * Create a new instance of {@link IIndexedField}
   * 
   * @param fieldName
   *          the field name to be used
   * @return
   */
  public static IIndexedField create(String fieldName) {
    return new IndexedField(fieldName);
  }

  /**
   * Return the name of the field
   * 
   * @return the field name
   */
  String getFieldName();

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

  default String getColumnName(IDataStore<?, ?> datastore) {
    return getColumnName(datastore.getMapperFactory().getMapper(getClass()));
  }

  default String getColumnName(IMapper<?> mapper) {
    String fieldName = getFieldName();
    String subFieldName = "";
    int i = fieldName.indexOf('.');
    if (i > 0) {
      subFieldName = fieldName.substring(i);
      fieldName = fieldName.substring(0, i);
    }
    IProperty field = mapper.getField(fieldName);
    if (field == null)
      throw new de.braintags.vertx.jomnigate.exception.NoSuchFieldException(getFieldName());
    return field.getColumnInfo().getName() + subFieldName;
  }

}
