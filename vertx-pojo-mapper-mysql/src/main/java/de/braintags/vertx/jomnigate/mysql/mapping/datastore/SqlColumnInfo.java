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

package de.braintags.vertx.jomnigate.mysql.mapping.datastore;

import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnHandler;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo;
import de.braintags.vertx.jomnigate.mapping.datastore.impl.DefaultColumnInfo;
import de.braintags.vertx.jomnigate.mysql.mapping.datastore.colhandler.AbstractSqlColumnHandler;
import io.vertx.core.json.JsonObject;

/**
 * An implementation of {@link IColumnInfo} for use with sql based datastores.
 * 
 * @author Michael Remme
 * 
 */

public class SqlColumnInfo extends DefaultColumnInfo {
  private static final String NUMERIC_PRECISION = "NUMERIC_PRECISION";
  private static final String NUMERIC_SCALE = "NUMERIC_SCALE";
  private static final String DATA_TYPE = "DATA_TYPE";
  private static final String IS_NULLABLE = "IS_NULLABLE";
  private static final String CHARACTER_MAXIMUM_LENGTH = "CHARACTER_MAXIMUM_LENGTH";

  /**
   * The default constructor to create an instance during the mapping
   * 
   * @param field
   * @param columnHandler
   */
  public SqlColumnInfo(IField field, IColumnHandler columnHandler) {
    super(field, columnHandler);
  }

  @Override
  protected void init(IField field, IColumnHandler columnHandler) {
    super.init(field, columnHandler);
    ((AbstractSqlColumnHandler) columnHandler).applyColumnMetaData(this);

  }

  /**
   * The constructor to create an instance by reading the information from the current state in the datastore
   * 
   * @param row
   *          the entry from a result set describing the column in the datastore
   */
  public SqlColumnInfo(JsonObject row) {
    super(row.getString("COLUMN_NAME"));
    init(row);
  }

  private void init(JsonObject row) {
    // [TABLE_CATALOG, TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, ORDINAL_POSITION, COLUMN_DEFAULT, IS_NULLABLE, DATA_TYPE,
    // CHARACTER_MAXIMUM_LENGTH, CHARACTER_OCTET_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE, DATETIME_PRECISION,
    // CHARACTER_SET_NAME,
    // COLLATION_NAME, COLUMN_TYPE, COLUMN_KEY, EXTRA, PRIVILEGES, COLUMN_COMMENT]
    Object nullable = row.getValue(IS_NULLABLE);
    setNullable(nullable != null && nullable.equals("YES"));
    if (row.containsKey(CHARACTER_MAXIMUM_LENGTH) && row.getInteger(CHARACTER_MAXIMUM_LENGTH) != null)
      setLength(row.getInteger(CHARACTER_MAXIMUM_LENGTH));
    if (row.containsKey(NUMERIC_PRECISION) && row.getInteger(NUMERIC_PRECISION) != null)
      setPrecision(row.getInteger(NUMERIC_PRECISION));
    if (row.containsKey(NUMERIC_SCALE) && row.getInteger(NUMERIC_SCALE) != null)
      setScale(row.getInteger(NUMERIC_SCALE));
    if (!row.containsKey(DATA_TYPE))
      throw new MappingException(
          String.format("Could not find the field %s in the row for column %s", DATA_TYPE, getName()));
    setType(row.getString(DATA_TYPE, null));
  }

  /**
   * This method copies the database metadata of the current instance into the destination
   * 
   * @param destination
   *          the destination to copy the meta data into
   */
  public void copyInto(SqlColumnInfo destination) {
    destination.setLength(getLength());
    destination.setNullable(isNullable());
    destination.setPrecision(getPrecision());
    destination.setScale(getScale());
    destination.setType(getType());
  }
}
