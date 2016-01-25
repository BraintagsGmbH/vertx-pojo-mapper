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
  protected StringBuilder generateColumn(IField field, IColumnInfo ci) {
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
   * de.braintags.io.vertx.util.pojomapper.mysql.mapping.datastore.colhandler.AbstractSqlColumnHandler#checkColumnModified(de
   * .braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo,
   * de.braintags.io.vertx.util.pojomapper.mapping.datastore.IColumnInfo)
   */
  @Override
  protected boolean checkColumnModified(IColumnInfo plannedCi, IColumnInfo existingCi) {
    return super.checkColumnModified(plannedCi, existingCi);
  }

}
