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

  private String type = Property.UNDEFINED_COLUMN_TYPE;
  private int length = Property.UNDEFINED_INTEGER;
  private int scale = Property.UNDEFINED_INTEGER;
  private int precision = Property.UNDEFINED_INTEGER;

  private boolean nullable = true;
  private boolean unique = false;

  /**
   * Initializes an instance by using a defined {@link Property} and adds the defined {@link IColumnHandler}
   * 
   * @param field
   *          the {@link IField} to be used for init
   * @param columnHandler
   *          the {@link IColumnHandler} to be used
   */
  public DefaultColumnInfo(IField field, IColumnHandler columnHandler) {
    this.columnHandler = columnHandler;
    colName = computePropertyName(field);
    init(field, columnHandler);
  }

  protected String computePropertyName(IField field) {
    Property prop = (Property) field.getAnnotation(Property.class);
    if (prop != null) {
      String propName = prop.value();
      if (!propName.equals(Property.UNDEFINED_COLUMN_NAME))
        return propName;
    }
    return field.getName();
  }

  private void init(IField field, IColumnHandler columnHandler) {
    Property prop = (Property) field.getAnnotation(Property.class);
    if (prop != null) {
      type = prop.columnType();
      length = prop.length();
      scale = prop.scale();
      precision = prop.precision();
      nullable = prop.nullable();
      unique = prop.unique();
    }
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

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo#getType()
   */
  @Override
  public String getType() {
    return type;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo#getLength()
   */
  @Override
  public int getLength() {
    return length;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo#getScale()
   */
  @Override
  public int getScale() {
    return scale;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo#getPrecision()
   */
  @Override
  public int getPrecision() {
    return precision;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo#isNullable()
   */
  @Override
  public boolean isNullable() {
    return nullable;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo#isUnique()
   */
  @Override
  public boolean isUnique() {
    return unique;
  }

}
