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

package de.braintags.io.vertx.pojomapper.mysql.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.ISyncResult;
import de.braintags.io.vertx.pojomapper.mapping.SyncAction;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;
import de.braintags.io.vertx.pojomapper.mapping.impl.DefaultSyncResult;
import de.braintags.io.vertx.pojomapper.mapping.impl.Mapper;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlColumnInfo;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlTableInfo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

/**
 * 
 * @author Michael Remme
 * 
 */

public class SqlDataStoreSynchronizer implements IDataStoreSynchronizer<String> {
  private static Logger LOGGER = LoggerFactory.getLogger(SqlDataStoreSynchronizer.class);

  private MySqlDataStore datastore;

  private static final String TABLE_QUERY = "SELECT * FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='%s' AND TABLE_NAME='%s'";
  private static final String COLUMN_QUERY = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS where TABLE_SCHEMA='%s' AND TABLE_NAME='%s'";
  private static final String CREATE_TABLE = "CREATE TABLE %s.%s ( %s )";

  /**
   * 
   */
  public SqlDataStoreSynchronizer(MySqlDataStore ds) {
    this.datastore = ds;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer#synchronize(de.braintags.io.vertx.pojomapper.
   * mapping .IMapper, io.vertx.core.Handler)
   */
  @Override
  public void synchronize(IMapper mapper, Handler<AsyncResult<ISyncResult<String>>> resultHandler) {
    datastore.getSqlClient().getConnection(cr -> {
      if (cr.failed()) {
        resultHandler.handle(Future.failedFuture(cr.cause()));
      } else {
        SQLConnection connection = cr.result();
        try {
          readTableFromDatabase(connection, mapper, res -> checkTable(connection, (Mapper) mapper, res, resultHandler));
        } finally {
          LOGGER.debug("closing connection - sync finished");
          connection.close();
        }
      }
    });

  }

  private void checkTable(SQLConnection connection, Mapper mapper, AsyncResult<ITableInfo> tableResult,
      Handler<AsyncResult<ISyncResult<String>>> resultHandler) {
    if (tableResult.failed()) {
      resultHandler.handle(Future.failedFuture(tableResult.cause()));
    } else {
      try {
        ITableInfo dbTable = tableResult.result();
        if (dbTable == null) {
          generateNewTable(connection, mapper, resultHandler);
        } else {
          compareTables(mapper, dbTable, resultHandler);
        }
      } catch (Exception e) {
        resultHandler.handle(Future.failedFuture(e));
      }
    }

  }

  private void compareTables(IMapper mapper, ITableInfo currentDbTable,
      Handler<AsyncResult<ISyncResult<String>>> resultHandler) {
    ITableInfo newTi = mapper.getTableInfo();
    List<String> deletedCols = checkDeletedCols(newTi, currentDbTable);
    List<String> newCols = checkNewCols(newTi, currentDbTable);
    List<String> modifiedCols = checkModifiedCols(newTi, currentDbTable);

    if (!newCols.isEmpty() || !modifiedCols.isEmpty()) {
      throw new UnsupportedOperationException();
    } else {
      resultHandler.handle(Future.succeededFuture(new DefaultSyncResult(SyncAction.NO_ACTION)));
    }

  }

  private List<String> checkModifiedCols(ITableInfo newTableInfo, ITableInfo currentDbTable) {
    List<String> modCols = new ArrayList<String>();
    List<String> newCols = newTableInfo.getColumnNames();
    for (String colName : newCols) {
      IColumnInfo newCol = newTableInfo.getColumnInfo(colName);
      IColumnInfo currCol = currentDbTable.getColumnInfo(colName);
      if (newCol.isModified(currCol))
        modCols.add(colName);
    }
    return modCols;
  }

  private List<String> checkNewCols(ITableInfo newTableInfo, ITableInfo currentDbTable) {
    List<String> planedCols = newTableInfo.getColumnNames();
    List<String> existingCols = currentDbTable.getColumnNames();
    for (String planedCol : planedCols) {
      if (existingCols.contains(planedCol))
        planedCols.remove(planedCol);

    }
    return planedCols;
  }

  private List<String> checkDeletedCols(ITableInfo newTableInfo, ITableInfo currentDbTable) {
    List<String> existingCols = currentDbTable.getColumnNames();

    List<String> newCols = newTableInfo.getColumnNames();
    for (String newCol : newCols) {
      existingCols.remove(newCol);
    }
    return existingCols;
  }

  /*
   * CREATE TABLE test.test2 (id INT NOT NULL AUTO_INCREMENT, name LONGTEXT, wahr BOOL, PRIMARY KEY (id))
   * 
   * CREATE TABLE test.TestTable ( id int(10) NOT NULL auto_increment, name varchar(25), PRIMARY KEY (id) )
   * ENGINE=InnoDB DEFAULT CHARSET=utf8;
   * 
   */
  private void generateNewTable(SQLConnection connection, Mapper mapper,
      Handler<AsyncResult<ISyncResult<String>>> resultHandler) {
    DefaultSyncResult syncResult = createSyncResult(mapper, SyncAction.CREATE);
    connection.execute(syncResult.getSyncCommand(), exec -> {
      if (exec.failed()) {
        LOGGER.error("error in executing command: " + syncResult.getSyncCommand());
        resultHandler.handle(Future.failedFuture(exec.cause()));
      } else {
        readTableFromDatabase(connection, mapper, tableResult -> {
          if (tableResult.failed()) {
            resultHandler.handle(Future.failedFuture(tableResult.cause()));
          } else {
            mapper.setTableInfo(tableResult.result());
            resultHandler.handle(Future.succeededFuture(syncResult));
          }
        });

      }
    });
  }

  private DefaultSyncResult createSyncResult(IMapper mapper, SyncAction action) {
    String columnPart = generateColumnPart(mapper);
    String tableName = mapper.getTableInfo().getName();
    String database = datastore.getDatabase();
    String sqlCommand = String.format(CREATE_TABLE, database, tableName, columnPart);
    DefaultSyncResult sr = new DefaultSyncResult(action, sqlCommand);
    return sr;
  }

  /**
   * Generates the part of the sequence, which is creating the columns id int(10) NOT NULL auto_increment, name
   * varchar(25), PRIMARY KEY (id)
   * 
   * @param mapper
   * @return
   */
  private String generateColumnPart(IMapper mapper) {
    StringBuilder buffer = new StringBuilder();
    IField idField = mapper.getIdField();
    ITableInfo ti = mapper.getTableInfo();
    Set<String> fieldNames = mapper.getFieldNames();

    for (String fieldName : fieldNames) {
      String colString = generateColumn(mapper, ti, fieldName);
      buffer.append(colString).append(", ");
    }
    buffer.append(String.format("PRIMARY KEY ( %s )", idField.getColumnInfo().getName()));
    return buffer.toString();
  }

  private String generateColumn(IMapper mapper, ITableInfo ti, String fieldName) {
    IField field = mapper.getField(fieldName);
    IColumnInfo ci = ti.getColumnInfo(field);
    IColumnHandler ch = ci.getColumnHandler();
    if (ch == null)
      throw new MappingException("Undefined column handler for field  " + field.getFullName());
    String colString = (String) ch.generate(field);
    if (colString == null || colString.isEmpty())
      throw new UnsupportedOperationException(
          String.format(" Did not generate column creation string for column '%s'", fieldName));
    return colString;
  }

  private void readTableFromDatabase(SQLConnection connection, IMapper mapper,
      Handler<AsyncResult<ITableInfo>> resultHandler) {
    String tableQuery = String.format(TABLE_QUERY, datastore.getDatabase(), mapper.getTableInfo().getName());
    executeCommand(connection, tableQuery, result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        ResultSet rs = result.result();
        SqlTableInfo tInfo = createTableInfo(mapper, rs);
        if (tInfo == null) { // signal that table doesn't exist
          resultHandler.handle(Future.succeededFuture(null));
          return;
        }
        String columnQuery = String.format(COLUMN_QUERY, datastore.getDatabase(), mapper.getTableInfo().getName());
        executeCommand(connection, columnQuery, colResult -> readColumns(mapper, tInfo, colResult, resultHandler));
      }
    });

  }

  /**
   * Reads the columns from the datastore and updates the infos into the {@link ITableInfo}
   * 
   * @param mapper
   *          the mapper
   * @param tInfo
   *          the instance of {@link ITableInfo}
   * @param result
   *          the {@link ResultSet}
   * @param resultHandler
   *          the handler to be called
   */
  private void readColumns(IMapper mapper, SqlTableInfo tInfo, AsyncResult<ResultSet> result,
      Handler<AsyncResult<ITableInfo>> resultHandler) {
    if (result.failed()) {
      resultHandler.handle(Future.failedFuture(result.cause()));
    } else {
      ResultSet rs = result.result();
      if (rs.getNumRows() == 0) {
        String message = String.format("No column definitions found for '%s'", tInfo.getName());
        resultHandler.handle(Future.failedFuture(new UnsupportedOperationException(message)));
        return;
      }

      try {
        List<JsonObject> rows = rs.getRows();
        for (JsonObject row : rows) {
          readColumn(row, tInfo);
        }
        resultHandler.handle(Future.succeededFuture(tInfo));
      } catch (Exception e) {
        resultHandler.handle(Future.failedFuture(e));
        return;
      }

    }
  }

  private void readColumn(JsonObject row, SqlTableInfo tInfo) {
    // [TABLE_CATALOG, TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, ORDINAL_POSITION, COLUMN_DEFAULT, IS_NULLABLE, DATA_TYPE,
    // CHARACTER_MAXIMUM_LENGTH, CHARACTER_OCTET_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE, DATETIME_PRECISION,
    // CHARACTER_SET_NAME,
    // COLLATION_NAME, COLUMN_TYPE, COLUMN_KEY, EXTRA, PRIVILEGES, COLUMN_COMMENT]
    String colName = row.getString("COLUMN_NAME");
    SqlColumnInfo ci = (SqlColumnInfo) tInfo.getColumnInfo(colName);
    if (ci == null)
      throw new NullPointerException(String.format("Could not find required column with name '%s'", colName));
    ci.setNullable(row.getBoolean("IS_NULLABLE"));
    ci.setLength(row.getInteger("CHARACTER_MAXIMUM_LENGTH"));
    ci.setPrecision(row.getInteger("NUMERIC_PRECISION", Property.UNDEFINED_INTEGER));
    ci.setScale(row.getInteger("NUMERIC_SCALE", Property.UNDEFINED_INTEGER));
    ci.setType(row.getString("DATA_TYPE", null));
  }

  private SqlTableInfo createTableInfo(IMapper mapper, ResultSet resultSet) {
    if (resultSet.getNumRows() == 0)
      return null;
    return new SqlTableInfo(mapper);
  }

  /**
   * Executed the given command and returns the {@link ResultSet} to the {@link Handler}
   * 
   * @param command
   * @param resultHandler
   */
  private void executeCommand(SQLConnection connection, String command, Handler<AsyncResult<ResultSet>> resultHandler) {
    connection.query(command, qr -> {
      if (qr.failed()) {
        resultHandler.handle(Future.failedFuture(qr.cause()));
      } else {
        ResultSet res = qr.result();
        resultHandler.handle(Future.succeededFuture(res));
      }
    });
  }
}
