/*-
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.dataaccess.query;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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
  @JsonCreator
  public static IIndexedField create(String fieldName) {
    return new IndexedField(fieldName);
  }

  /**
   * Return the name of the field
   * 
   * @return the field name
   */
  @JsonValue
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

  /**
   * Returns the column name for an indexed field, which may in some cases differ from the field name
   * 
   * @param pojoClass
   *          the class that is mapped
   * @param datastore
   *          the datastore to retrieve the mapper from, which has the column information
   * @return the column name for this field
   */
  default String getColumnName(Class<?> pojoClass, IDataStore<?, ?> datastore) {
    return getColumnName(datastore.getMapperFactory().getMapper(pojoClass));
  }

  /**
   * Returns the column name for an indexed field, which may in some cases differ from the field name
   * 
   * @param mapper
   *          the mapper which has the column information for the field
   * @return the column name for this field
   */
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
