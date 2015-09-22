/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler;

import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.impl.AbstractColumnHandler;

/**
 * An abstract implementation for use with SQL based datastores The implementation checks, wether
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractSqlColumnHandler extends AbstractColumnHandler {
  private static final String ID_COLUMN_STRING = "%s INT(%d) NOT NULL auto_increment";

  /**
   * @param classesToDeal
   */
  public AbstractSqlColumnHandler(Class<?>... classesToDeal) {
    super(classesToDeal);
  }

  @Override
  public final Object generate(IField field) {
    IColumnInfo ci = field.getColumnInfo();
    if (field.getMapper().getIdField() == field) {
      return generateIdColumn(field, ci);
    } else {
      StringBuilder colString = generateColumn(field, ci);
      addNotNull(colString, ci);
      addUnique(colString, ci);
      return colString.toString();
    }
  }

  protected void addNotNull(StringBuilder colString, IColumnInfo ci) {
    if (!ci.isNullable())
      colString.append(" NOT NULL");
  }

  protected void addUnique(StringBuilder colString, IColumnInfo ci) {
    if (ci.isUnique())
      throw new UnsupportedOperationException("not yet supported: unique property");
  }

  // LONGTEXT DEFAULT zzzzz NOT NULL

  /**
   * Generates a sequence like "id int(10) NOT NULL auto_increment"
   * 
   * @param field
   * @return
   */
  protected String generateIdColumn(IField field, IColumnInfo ci) {
    String propName = ci.getName();
    int scale = ci.getScale();
    scale = scale == Property.UNDEFINED_INTEGER ? 10 : scale;
    return String.format(ID_COLUMN_STRING, propName, scale);
  }

  /**
   * Get the defined length of the {@link IColumnInfo}. If this value is undefined, then the given default value is
   * returned
   * 
   * @param ci
   * @param defaultValue
   * @return
   */
  public int getLength(IColumnInfo ci, int defaultValue) {
    return ci.getLength() == Property.UNDEFINED_INTEGER ? defaultValue : ci.getLength();
  }

  /**
   * Generate the sequence to build a column inside the datastore
   * 
   * @param field
   * @return
   */
  protected abstract StringBuilder generateColumn(IField field, IColumnInfo ci);
}
