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

import java.util.Set;

import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

/**
 * 
 * @author Michael Remme
 * 
 */

public class SqlDataStoreSynchronizer implements IDataStoreSynchronizer {
  private static Logger LOGGER = LoggerFactory.getLogger(SqlDataStoreSynchronizer.class);

  private MySqlDataStore datastore;

  private static final String TABLE_QUERY = "SELECT * FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='%s' AND TABLE_NAME='%s'";
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
  public void synchronize(IMapper mapper, Handler<AsyncResult<Void>> resultHandler) {
    readTableFromDatabase(mapper, res -> {
      if (res.failed()) {
        resultHandler.handle(Future.failedFuture(res.cause()));
      } else {
        try {
          ITableInfo dbTable = res.result();
          if (dbTable == null) {
            generateNewTable(mapper, resultHandler);
          } else {
            compareTables(mapper, dbTable);
          }
        } catch (Exception e) {
          resultHandler.handle(Future.failedFuture(e));
        }
      }
    });
  }

  /*
   * CREATE TABLE test.test2 (id INT NOT NULL AUTO_INCREMENT, name LONGTEXT, wahr BOOL, PRIMARY KEY (id))
   * 
   * CREATE TABLE test.TestTable ( id int(10) NOT NULL auto_increment, name varchar(25), PRIMARY KEY (id) )
   * ENGINE=InnoDB DEFAULT CHARSET=utf8;
   * 
   */

  private void generateNewTable(IMapper mapper, Handler<AsyncResult<Void>> resultHandler) {
    String columnPart = generateColumnPart(mapper);
    String tableName = mapper.getTableInfo().getName();
    String database = datastore.getDatabase();
    String sqlCommand = String.format(CREATE_TABLE, tableName, database, columnPart);
    datastore.getSqlClient().getConnection(cr -> {
      if (cr.failed()) {
        resultHandler.handle(Future.failedFuture(cr.cause()));
      } else {
        SQLConnection connection = cr.result();
        connection.execute(sqlCommand, exec -> {
          if (exec.failed()) {
            resultHandler.handle(exec);
          } else {
            // TODO perhaps improve result by searching in INFORMATION_SCHEMA?
            return;
          }
        });
      }
    });
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
      buffer.append(generateColumn(mapper, ti, fieldName)).append(", ");
    }
    buffer.append(String.format("PRIMARY KEY ( %s )", idField.getColumnInfo().getName()));
    return buffer.toString();
  }

  private String generateColumn(IMapper mapper, ITableInfo ti, String fieldName) {
    IField field = mapper.getField(fieldName);
    IColumnInfo ci = ti.getColumnInfo(field);
    IColumnHandler ch = ci.getColumnHandler();
    if (ch == null)
      throw new MappingException("Undefined column hanbdler for field " + field.getFullName());
    return (String) ch.generate(field);
  }

  private void compareTables(IMapper mapper, ITableInfo currentDbTable) {
    throw new UnsupportedOperationException();
  }

  private void readTableFromDatabase(IMapper mapper, Handler<AsyncResult<ITableInfo>> resultHandler) {
    String command = String.format(TABLE_QUERY, datastore.getDatabase(), mapper.getTableInfo().getName());
    executeCommand(command, result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        ResultSet rs = result.result();
        resultHandler.handle(Future.succeededFuture(createTableInfo(rs)));
      }
    });

  }

  private ITableInfo createTableInfo(ResultSet resultSet) {
    if (resultSet.getNumRows() == 0)
      return null;
    throw new UnsupportedOperationException();
  }

  /**
   * Executed the given command and returns the {@link ResultSet} to the {@link Handler}
   * 
   * @param command
   * @param resultHandler
   */
  private void executeCommand(String command, Handler<AsyncResult<ResultSet>> resultHandler) {
    AsyncSQLClient client = datastore.getSqlClient();
    client.getConnection(connectionResult -> {
      if (connectionResult.failed()) {
        LOGGER.error("", connectionResult.cause());
        resultHandler.handle(Future.failedFuture(connectionResult.cause()));
      } else {
        SQLConnection connection = connectionResult.result();
        connection.query(command, qr -> {
          try {
            if (qr.failed()) {
              resultHandler.handle(Future.failedFuture(qr.cause()));
            } else {
              ResultSet res = qr.result();
              resultHandler.handle(Future.succeededFuture(res));
            }
          } finally {
            LOGGER.debug("closing connection - sync finished");
            connection.close();
          }
        });
      }
    });
  }

}
