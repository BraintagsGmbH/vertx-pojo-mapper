/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mapping.datastore.impl;

import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class DefaultColumnInfo implements IColumnInfo {
  private String colName;
  private IColumnHandler columnHandler;

  /**
   * 
   */
  public DefaultColumnInfo(IField field, IColumnHandler columnHandler) {
    colName = computePropertyName(field);
    this.columnHandler = columnHandler;
  }

  protected String computePropertyName(IField field) {
    if (field.getField().isAnnotationPresent(Property.class)) {
      Property prop = field.getField().getAnnotation(Property.class);
      return prop.value();
    }
    return field.getName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo#getName()
   */
  @Override
  public String getName() {
    return colName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo#getFieldGenerator()
   */
  @Override
  public IColumnHandler getColumnHandler() {
    return columnHandler;
  }

}
