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

package de.braintags.io.vertx.pojomapper.mysql.mapping.datastore;

import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultColumnInfo;
import io.vertx.core.json.JsonObject;

/**
 * An implementation of {@link IColumnInfo} for use with sql based datastores.
 * 
 * @author Michael Remme
 * 
 */

public class SqlColumnInfo extends DefaultColumnInfo {

  /**
   * The default constructor to create an instance during the mapping
   * 
   * @param field
   * @param columnHandler
   */
  public SqlColumnInfo(IField field, IColumnHandler columnHandler) {
    super(field, columnHandler);
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
    setNullable(row.getBoolean("IS_NULLABLE"));
    setLength(row.getInteger("CHARACTER_MAXIMUM_LENGTH"));
    setPrecision(row.getInteger("NUMERIC_PRECISION", Property.UNDEFINED_INTEGER));
    setScale(row.getInteger("NUMERIC_SCALE", Property.UNDEFINED_INTEGER));
    setType(row.getString("DATA_TYPE", null));
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
