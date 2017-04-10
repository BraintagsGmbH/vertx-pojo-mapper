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

import de.braintags.vertx.jomnigate.dataaccess.query.impl.IndexedField;

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
