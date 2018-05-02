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

package de.braintags.vertx.jomnigate.mysql.mapping;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.braintags.vertx.jomnigate.annotation.EntityOption;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.mapping.IIndexDefinition;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.ISyncResult;
import de.braintags.vertx.jomnigate.mapping.SyncAction;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnHandler;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo;
import de.braintags.vertx.jomnigate.mapping.datastore.ITableInfo;
import de.braintags.vertx.jomnigate.mapping.impl.AbstractDataStoreSynchronizer;
import de.braintags.vertx.jomnigate.mapping.impl.DefaultSyncCommand;
import de.braintags.vertx.jomnigate.mapping.impl.DefaultSyncResult;
import de.braintags.vertx.jomnigate.mapping.impl.Mapper;
import de.braintags.vertx.jomnigate.mysql.MySqlDataStore;
import de.braintags.vertx.jomnigate.mysql.SqlUtil;
import de.braintags.vertx.jomnigate.mysql.mapping.datastore.SqlTableInfo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.ResultSet;

/**
 * Used to synchronize tables inside the database with the POJOs
 * 
 * @author Michael Remme
 * 
 */

public class SqlDataStoreSynchronizer extends AbstractDataStoreSynchronizer<String> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SqlDataStoreSynchronizer.class);
  private final DefaultSyncResult internalSyncResult = new DefaultSyncResult();

  private final MySqlDataStore datastore;

  private static final String TABLE_QUERY = "SELECT * FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='%s' AND TABLE_NAME='%s'";
  private static final String COLUMN_QUERY = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS where TABLE_SCHEMA='%s' AND TABLE_NAME='%s'";
  private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %s.%s ( %s ) %s";

  /**
   * Create a new instance
   * 
   * @param ds
   *          the {@link MySqlDataStore} used
   */
  public SqlDataStoreSynchronizer(final MySqlDataStore ds) {
    this.datastore = ds;
  }

  @Override
  public void syncTable(final IMapper mapper, final Handler<AsyncResult<Void>> resultHandler) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("starting synchronization for mapper " + mapper.getClass().getSimpleName());
    }
    readTableFromDatabase(mapper, res -> checkTable((Mapper) mapper, res, result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        getSyncResult().addCommand(result.result());
        resultHandler.handle(Future.succeededFuture());
      }
    }));
  }

  private void checkTable(final Mapper mapper, final AsyncResult<SqlTableInfo> tableResult,
      final Handler<AsyncResult<DefaultSyncCommand>> resultHandler) {
    if (tableResult.failed()) {
      resultHandler.handle(Future.failedFuture(tableResult.cause()));
    } else {
      try {
        SqlTableInfo dbTable = tableResult.result();
        if (dbTable == null) {
          generateNewTable(mapper, resultHandler);
        } else {
          compareTables(mapper, dbTable, resultHandler);
        }
      } catch (Exception e) {
        resultHandler.handle(Future.failedFuture(e));
      }
    }

  }

  private void compareTables(final IMapper mapper, final SqlTableInfo currentDbTable,
      final Handler<AsyncResult<DefaultSyncCommand>> resultHandler) {
    Map<String, SyncAction> syncMap = currentDbTable.compareColumns(mapper);
    if (!syncMap.isEmpty()) {
      logSyncMap(mapper, syncMap);
      throw new UnsupportedOperationException("Implement update of table structure in mapper "
          + mapper.getMapperClass().getName() + ": " + syncMap.toString());
    } else {
      resultHandler.handle(Future.succeededFuture());
    }
  }

  private void logSyncMap(final IMapper mapper, final Map<String, SyncAction> syncMap) {
    LOGGER.info(mapper.getMapperClass().getName() + ": " + syncMap);
  }

  /*
   * CREATE TABLE test.test2 (id INT NOT NULL AUTO_INCREMENT, name LONGTEXT, wahr BOOL, PRIMARY KEY (id))
   * 
   * CREATE TABLE test.TestTable ( id int(10) NOT NULL auto_increment, name varchar(25), PRIMARY KEY (id) )
   * ENGINE=InnoDB DEFAULT CHARSET=utf8;
   * 
   */
  private void generateNewTable(final Mapper mapper, final Handler<AsyncResult<DefaultSyncCommand>> resultHandler) {
    DefaultSyncCommand syncCommand = createSyncCommand(mapper, SyncAction.CREATE);
    SqlUtil.query(datastore, syncCommand.getCommand(), exec -> {
      if (exec.failed()) {
        LOGGER.error("error in executing command: " + syncCommand.getCommand());
        resultHandler.handle(Future.failedFuture(exec.cause()));
      } else {
        readTableFromDatabase(mapper, tableResult -> {
          if (tableResult.failed()) {
            resultHandler.handle(Future.failedFuture(tableResult.cause()));
          } else {
            tableResult.result().copyInto(mapper);
            resultHandler.handle(Future.succeededFuture(syncCommand));
          }
        });

      }
    });
  }

  private DefaultSyncCommand createSyncCommand(final IMapper mapper, final SyncAction action) {
    String columnPart = generateColumnPart(mapper);
    String tableName = mapper.getTableInfo().getName();
    String database = datastore.getDatabase();
    String sqlCommand = String.format(CREATE_TABLE, database, tableName, columnPart, getOptions(mapper));
    return new DefaultSyncCommand(action, sqlCommand);
  }

  private String getOptions(final IMapper mapper) {
    Buffer buffer = Buffer.buffer();
    for (EntityOption option : mapper.getEntity().options()) {
      if ("ENGINE".equalsIgnoreCase(option.key()) || "DEFAULT CHARSET".equalsIgnoreCase(option.key())) {
        buffer.appendString(option.key()).appendString("=").appendString(option.value()).appendString(" ");
      } else {
        LOGGER.info("UNHANDLED OPTION: " + option.key());
      }
    }
    return buffer.toString();
  }

  /**
   * Generates the part of the sequence, which is creating the columns id int(10) NOT NULL auto_increment, name
   * varchar(25), PRIMARY KEY (id)
   * 
   * @param mapper
   * @return
   */
  private String generateColumnPart(final IMapper mapper) {
    StringBuilder buffer = new StringBuilder();
    IProperty idField = mapper.getIdInfo().getField();
    ITableInfo ti = mapper.getTableInfo();
    Set<String> fieldNames = mapper.getFieldNames();

    for (String fieldName : fieldNames) {
      String colString = generateColumn(mapper, ti, fieldName);
      buffer.append(colString).append(", ");
    }
    buffer.append(String.format("PRIMARY KEY ( %s )", idField.getColumnInfo().getName()));
    return buffer.toString();
  }

  private String generateColumn(final IMapper mapper, final ITableInfo ti, final String fieldName) {
    IProperty field = mapper.getField(fieldName);
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

  private void readTableFromDatabase(final IMapper mapper, final Handler<AsyncResult<SqlTableInfo>> resultHandler) {
    String tableQuery = String.format(TABLE_QUERY, datastore.getDatabase(), mapper.getTableInfo().getName());
    SqlUtil.query(datastore, tableQuery, result -> {
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
        SqlUtil.query(datastore, columnQuery, colResult -> readColumns(tInfo, colResult, resultHandler));
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
  private void readColumns(final SqlTableInfo tInfo, final AsyncResult<ResultSet> result,
      final Handler<AsyncResult<SqlTableInfo>> resultHandler) {
    if (result.failed()) {
      resultHandler.handle(Future.failedFuture(result.cause()));
    } else {
      ResultSet rs = result.result();
      if (rs.getNumRows() == 0) {
        String message = String.format("No column definitions found for '%s'", tInfo.getName());
        resultHandler.handle(Future.failedFuture(new MappingException(message)));
        return;
      }

      try {
        List<JsonObject> rows = rs.getRows();
        for (JsonObject row : rows) {
          tInfo.createColumnInfo(row);
        }
        resultHandler.handle(Future.succeededFuture(tInfo));
      } catch (Exception e) {
        resultHandler.handle(Future.failedFuture(e));
        return;
      }
    }
  }

  private SqlTableInfo createTableInfo(final IMapper mapper, final ResultSet resultSet) {
    if (resultSet.getNumRows() == 0)
      return null;
    return new SqlTableInfo(mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.impl.AbstractDataStoreSynchronizer#getSyncResult()
   */
  @Override
  protected ISyncResult<String> getSyncResult() {
    return internalSyncResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.impl.AbstractDataStoreSynchronizer#syncIndexes(de.braintags.vertx.
   * pojomapper.mapping.IMapper, de.braintags.vertx.jomnigate.annotation.Indexes, io.vertx.core.Handler)
   */
  @Override
  protected void syncIndexes(final IMapper<?> mapper, final ImmutableSet<IIndexDefinition> indexes,
      final Handler<AsyncResult<Void>> resultHandler) {
    SqlUtil.createIndexes(datastore, mapper.getTableInfo().getName(), mapper.getIndexDefinitions(), result -> {
      if (result.failed()) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Error creating indexes: " + result.cause());
        }
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Indexes created: " + result.result());
        }
        resultHandler.handle(Future.succeededFuture());
      }
    });
  }

}
