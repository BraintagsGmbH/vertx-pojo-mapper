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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.SyncAction;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnHandler;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo;
import de.braintags.vertx.jomnigate.mapping.datastore.ITableInfo;
import de.braintags.vertx.jomnigate.mapping.datastore.impl.DefaultTableInfo;
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
   * de.braintags.vertx.jomnigate.mapping.datastore.impl.DefaultTableInfo#generateColumnInfo(de.braintags.vertx.util.
   * pojomapper.mapping.IField, de.braintags.vertx.jomnigate.mapping.datastore.IColumnHandler)
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
   * This method copies the database metadata of the current instance into the {@link SqlTableInfo} of the
   * {@link IMapper}. That way, the current instance should be an instance which was created by the information inside
   * the datastore
   * 
   * @param mapper
   *          the {@link ITableInfo} of the mapper will be the destination to copy the meta data into
   */
  public void copyInto(IMapper mapper) {
    SqlTableInfo destination = (SqlTableInfo) mapper.getTableInfo();
    Iterator<String> it = getColumnNames().iterator();
    while (it.hasNext()) {
      String colName = it.next();
      SqlColumnInfo sourceCol = (SqlColumnInfo) getColumnInfo(colName);
      SqlColumnInfo destinationCol = (SqlColumnInfo) destination.getColumnInfo(colName);
      sourceCol.copyInto(destinationCol);
    }
  }

  /**
   * Compares the columns of the current {@link ITableInfo} with the ones of the mapper
   * 
   * @param mapper
   *          the mapper, which contains the {@link ITableInfo} to be compared
   * @return A map with the names of the modified, deleted or new columns and the {@link SyncAction}
   */
  public Map<String, SyncAction> compareColumns(IMapper mapper) {
    Map<String, SyncAction> ret = new HashMap<>();
    SqlTableInfo newTi = (SqlTableInfo) mapper.getTableInfo();
    List<String> deletedCols = checkDeletedCols(newTi);
    List<String> newCols = checkNewCols(newTi);
    List<String> modifiedCols = checkModifiedCols(newTi);

    for (String colName : deletedCols) {
      ret.put(colName, SyncAction.DELETE);
    }

    for (String colName : modifiedCols) {
      ret.put(colName, SyncAction.UPDATE);
    }

    for (String colName : newCols) {
      ret.put(colName, SyncAction.CREATE);
    }
    return ret;
  }

  private List<String> checkModifiedCols(SqlTableInfo newTableInfo) {
    List<String> modCols = new ArrayList<String>();
    List<String> newCols = newTableInfo.getColumnNames();
    for (String colName : newCols) {
      IColumnInfo newCol = newTableInfo.getColumnInfo(colName);
      IColumnInfo currCol = getColumnInfo(colName);
      boolean modified = newCol.getColumnHandler().isColumnModified(newCol, currCol);
      if (modified)
        modCols.add(colName);
      String test = "";
    }
    return modCols;
  }

  private List<String> checkNewCols(ITableInfo newTableInfo) {
    List<String> returnList = new ArrayList<String>();
    List<String> planedCols = newTableInfo.getColumnNames();
    List<String> existingCols = getColumnNames();
    for (String planedCol : planedCols) {
      if (!existingCols.contains(planedCol))
        returnList.add(planedCol);
    }
    return returnList;
  }

  private List<String> checkDeletedCols(ITableInfo newTableInfo) {
    List<String> existingCols = getColumnNames();

    List<String> newCols = newTableInfo.getColumnNames();
    for (String newCol : newCols) {
      existingCols.remove(newCol);
    }
    return existingCols;
  }

}
