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

import java.sql.Timestamp;
import java.util.Calendar;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlColumnInfo;

/**
 * ColumnHandler dealing with {@link Timestamp}
 * 
 * @author Michael Remme
 * 
 */

public class TimestampColumnHandler extends AbstractSqlColumnHandler {
  public static final String TIMESTAMP_TYPE = "TIMESTAMP";

  /**
   * Constructor
   */
  public TimestampColumnHandler() {
    super(Timestamp.class, Calendar.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.AbstractSqlColumnHandler#applyMetaData(de.
   * braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlColumnInfo)
   */
  @Override
  public void applyMetaData(SqlColumnInfo column) {
    column.setType(TIMESTAMP_TYPE);
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
    return new StringBuilder(String.format("%s %s ", ci.getName(), ci.getType()));
  }

}
