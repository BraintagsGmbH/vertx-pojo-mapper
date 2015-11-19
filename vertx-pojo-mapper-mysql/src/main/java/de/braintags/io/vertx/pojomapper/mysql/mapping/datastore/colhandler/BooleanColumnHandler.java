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

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlColumnInfo;

/**
 * 
 * @author Michael Remme
 * 
 */

public class BooleanColumnHandler extends AbstractSqlColumnHandler {
  public static final String BOOLEAN_TYPE = "BOOLEAN";

  /**
   * @param classesToDeal
   */
  public BooleanColumnHandler() {
    super(boolean.class, Boolean.class);
  }

  @Override
  public void applyMetaData(SqlColumnInfo column) {
    column.setType(BOOLEAN_TYPE);
  }

  @Override
  protected StringBuilder generateColumn(IField field, IColumnInfo ci) {
    return new StringBuilder(String.format("%s %s ", ci.getName(), ci.getType()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.AbstractSqlColumnHandler#checkColumnModified(de
   * .braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo,
   * de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo)
   */
  @Override
  protected boolean checkColumnModified(IColumnInfo plannedCi, IColumnInfo existingCi) {
    return super.checkColumnModified(plannedCi, existingCi)
        && !existingCi.getType().equalsIgnoreCase(ByteColumnHandler.TINYINT_TYPE);
  }

}
