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
package de.braintags.vertx.jomnigate.dataaccess.query.impl;

import de.braintags.vertx.jomnigate.dataaccess.query.IIndexedField;

/**
 * Default implementation of {@link IIndexedField}
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

  @Override
  public String toString() {
    return fieldName;
  }
}
