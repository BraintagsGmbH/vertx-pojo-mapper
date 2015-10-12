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
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlColumnInfo;

/**
 * 
 * @author Michael Remme
 * 
 */

public abstract class NumericColumnHandler extends AbstractSqlColumnHandler {
  private String colType;
  private boolean usePrecision;
  private boolean useScale;

  /**
   * @param classesToDeal
   */
  public NumericColumnHandler(String colType, boolean usePrecision, boolean useScale, Class<?>... classesToDeal) {
    super(classesToDeal);
    this.colType = colType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.AbstractSqlColumnHandler#applyMetaData(de.
   * braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlColumnInfo)
   */
  @Override
  public void applyMetaData(SqlColumnInfo ci) {
    if (ci.getType() == null || ci.getType().isEmpty())
      ci.setType(colType);
    if (!useScale)
      ci.setScale(Property.UNDEFINED_INTEGER);
    if (!usePrecision)
      ci.setPrecision(Property.UNDEFINED_INTEGER);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.AbstractSqlColumnHandler#generateColumn(de.
   * braintags.io.vertx.pojomapper.mapping.IField, de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo)
   */
  @Override
  protected StringBuilder generateColumn(IField field, IColumnInfo ci) {
    if (ci.getPrecision() == Property.UNDEFINED_INTEGER)
      return new StringBuilder(String.format("%s %s ", ci.getName(), ci.getType()));
    if (ci.getScale() == Property.UNDEFINED_INTEGER)
      return new StringBuilder(String.format("%s %s( %d ) ", ci.getName(), ci.getType(), ci.getPrecision()));
    return new StringBuilder(
        String.format("%s %s( %d, %d ) ", ci.getName(), ci.getType(), ci.getPrecision(), ci.getScale()));
  }

  // Tinyint -128 to 127
  // SMALLINT -32768 to 32767
  // INT / INTEGER -2147483648 to 2147483647
  // BIGINT -9223372036854775808 to 9223372036854775807
  // DOUBLE

}
