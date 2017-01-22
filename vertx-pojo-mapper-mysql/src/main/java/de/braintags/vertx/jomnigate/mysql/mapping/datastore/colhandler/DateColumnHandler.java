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
package de.braintags.vertx.jomnigate.mysql.mapping.datastore.colhandler;

import java.util.Calendar;
import java.util.Date;

import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo;
import de.braintags.vertx.jomnigate.mysql.mapping.datastore.SqlColumnInfo;

/**
 * ColumnHandler dealing with instances of {@link Date} and {@link Calendar}
 * 
 * @author Michael Remme
 * 
 */

public class DateColumnHandler extends AbstractSqlColumnHandler {
  public static final String DATE_TYPE = "DATETIME";

  /**
   * Constructor
   */
  public DateColumnHandler() {
    super(Date.class, Calendar.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mysql.mapping.datastore.colhandler.AbstractSqlColumnHandler#applyMetaData(de.
   * braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlColumnInfo)
   */
  @Override
  public void applyMetaData(SqlColumnInfo column) {
    column.setType(DATE_TYPE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.mysql.mapping.datastore.colhandler.AbstractSqlColumnHandler#generateColumn(de.
   * braintags.io.vertx.pojomapper.mapping.IField, de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo)
   */
  @Override
  protected StringBuilder generateColumn(IField field, IColumnInfo ci) {
    return new StringBuilder(String.format("%s %s (3) ", ci.getName(), ci.getType()));
  }

}
