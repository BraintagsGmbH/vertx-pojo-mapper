/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.vertx.jomnigate.sql.mapping.colhandler;

import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo;
import de.braintags.vertx.jomnigate.sql.mapping.SqlColumnInfo;

/**
 * Handles char and Character
 * 
 * @author Michael Remme
 * 
 */

public class CharColumnHandler extends AbstractSqlColumnHandler {
  public static final String CHAR_TYPE = "CHAR";

  /**
   * Constructor for a ByteColumnHandler
   */
  public CharColumnHandler() {
    super(Character.class, char.class);
  }

  @Override
  protected StringBuilder generateColumn(IProperty field, IColumnInfo ci) {
    return new StringBuilder(String.format("%s CHAR( 1 ) ", ci.getName()));
  }

  @Override
  public void applyMetaData(SqlColumnInfo column) {
    column.setType(CHAR_TYPE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.mysql.mapping.datastore.colhandler.AbstractSqlColumnHandler#checkColumnModified(de.
   * braintags.vertx.jomnigate.mapping.datastore.IColumnInfo,
   * de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo)
   */
  @Override
  protected boolean checkColumnModified(IColumnInfo plannedCi, IColumnInfo existingCi) {
    return super.checkColumnModified(plannedCi, existingCi);
  }

}
