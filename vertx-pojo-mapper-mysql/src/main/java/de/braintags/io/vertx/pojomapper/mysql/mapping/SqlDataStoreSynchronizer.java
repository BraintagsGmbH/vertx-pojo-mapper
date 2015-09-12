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
  private static Logger logger = LoggerFactory.getLogger(SqlDataStoreSynchronizer.class);

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
        } catch (Throwable e) {
          resultHandler.handle(Future.failedFuture(e));
        }
      }
    });
  }

  /*
   * CREATE TABLE test.test2 (id INT NOT NULL AUTO_INCREMENT, name LONGTEXT, wahr BOOL, PRIMARY KEY (id))
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
            // perhaps improve result by searching in INFORMATION_SCHEMA?
            return;
          }
        });
      }
    });
  }

  private String generateColumnPart(IMapper mapper) {
    StringBuffer buffer = new StringBuffer();
    IField field = mapper.getIdField();
    ITableInfo ti = mapper.getTableInfo();
    Set<String> fieldNames = mapper.getFieldNames();
    for (String fieldName : fieldNames) {
      IColumnInfo ci = ti.getColumnInfo(fieldName);
      IColumnHandler ch = ci.getColumnHandler();

    }

    throw new UnsupportedOperationException();

  }

  private void compareTables(IMapper mapper, ITableInfo currentDbTable) {
    throw new UnsupportedOperationException();
  }

  private void readTableFromDatabase(IMapper mapper, Handler<AsyncResult<ITableInfo>> resultHandler) {
    AsyncSQLClient client = datastore.getSqlClient();

    client.getConnection(connectionResult -> {
      if (connectionResult.failed()) {
        logger.error("", connectionResult.cause());
        resultHandler.handle(Future.failedFuture(connectionResult.cause()));
      } else {
        SQLConnection connection = connectionResult.result();
        String command = String.format(TABLE_QUERY, datastore.getDatabase(), mapper.getTableInfo().getName());
        connection.query(command, qr -> {
          try {
            if (qr.failed()) {
              resultHandler.handle(Future.failedFuture(qr.cause()));
            } else {
              ResultSet res = qr.result();
              if (res.getNumRows() < 1) {
                resultHandler.handle(Future.succeededFuture());
              } else {
                resultHandler.handle(Future.failedFuture(new UnsupportedOperationException()));

              }
            }
          } finally {
            logger.debug("closing connection - sync finished");
            connection.close();
          }
        });
      }
    });

  }
}
