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

import java.util.Iterator;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultTableInfo;
import io.vertx.core.json.JsonObject;

/**
 * An implementation of {@link ITableInfo} for use with sql based datastores
 * 
 * @author Michael Remme
 * 
 */

public class SqlTableInfo extends DefaultTableInfo {

  /**
   * @param mapper
   */
  public SqlTableInfo(IMapper mapper) {
    super(mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultTableInfo#generateColumnInfo(de.braintags.io.vertx.
   * pojomapper.mapping.IField, de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler)
   */
  @Override
  protected IColumnInfo generateColumnInfo(IField field, IColumnHandler columnHandler) {
    return new SqlColumnInfo(field, columnHandler);
  }

  /**
   * This method is used, when information are read from the datastore to describe the current state of the table inside
   * the datastore. It can only be used during synchronization to compare the current state with the planned state and
   * it does NOT contain the needed {@link IColumnHandler}
   * 
   * @param row
   *          the entry from a result set
   */
  public void createColumnInfo(JsonObject row) {
    addColumnInfo(new SqlColumnInfo(row));
  }

  /**
   * This method copies the database metadata of the current instance into the destination, including the metadata of
   * each {@link IColumnInfo}
   * 
   * @param destination
   *          the destination to copy the meta data into
   */
  public void copyInto(SqlTableInfo destination) {
    Iterator<String> it = getColumnNames().iterator();
    while (it.hasNext()) {
      String colName = it.next();
      SqlColumnInfo sourceCol = (SqlColumnInfo) getColumnInfo(colName);
      SqlColumnInfo destinationCol = (SqlColumnInfo) destination.getColumnInfo(colName);
      sourceCol.copyInto(destinationCol);
    }
  }

}
