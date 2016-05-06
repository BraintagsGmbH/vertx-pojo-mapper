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

package de.braintags.io.vertx.pojomapper.mysql;

import java.util.Arrays;
import java.util.List;

import de.braintags.io.vertx.pojomapper.annotation.Index;
import de.braintags.io.vertx.pojomapper.annotation.Indexes;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mysql.exception.SqlException;
import de.braintags.io.vertx.util.CounterObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

/**
 * Utility methods for sql
 * 
 * @author Michael Remme
 * 
 */

public class SqlUtil {
  private static final String GAINED_SUCCESSFULLY_A_CONNECTION = "gained successfully a connection";
  private static final String ERROR_EXECUTING_COMMAND_STATEMENT = "error executing command. Statement: ";
  private static final String ERROR_GAINING_CONNECTION = "error gaining connection";
  private static final String CONNECTION_CLOSED = "connection closed";
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(SqlUtil.class);
  private static final String COMMAND_SUCCESS = "command successful executed";

  private static final List<String> NUMBERIC_TYPES = Arrays.asList("INT", "INTEGER", "TINYINT", "DOUBLE", "BIGINT",
      "SMALLINT", "MEDIUMINT", "FLOAT", "REAL", "DECIMAL", "NUMERIC");
  private static final List<String> CHARACTER_TYPES = Arrays.asList("CHAR", "VARCHAR", "LONGTEXT", "TEXT");
  private static final List<String> DATE_TYPES = Arrays.asList("DATE", "DATETIME", "TIMESTAMP", "TIME", "YEAR");

  private SqlUtil() {
  }

  /**
   * Create indexes which are defined by the given {@link Index}
   * 
   * @param ds
   *          the datastore
   * @param collection
   *          the name of the collection to be used
   * @param indexes
   *          the index definition
   * @param handler
   *          the handler to be informed
   */
  public static final void createIndexes(MySqlDataStore ds, String tableName, Indexes indexes,
      Handler<AsyncResult<String>> handler) {
    if (indexes == null || indexes.value().length == 0) {
      handler.handle(Future.succeededFuture("No indexes defined"));
    } else {
      CounterObject<String> co = new CounterObject<>(indexes.value().length, handler);
      Buffer returnBuffer = Buffer.buffer();
      for (Index index : indexes.value()) {
        if (co.isError()) {
          break;
        }
        createIndex(ds, tableName, index, result -> {
          if (result.failed()) {
            co.setThrowable(result.cause());
          } else {
            returnBuffer.appendString(result.result());
            if (co.reduce()) {
              co.setResult(returnBuffer.toString());
            }
          }
        });
      }
    }

  }

  public static final short INDEX_EXISTS = 0;
  public static final short INDEX_NOT_EXISTS = 1;
  public static final short INDEX_MODIFIED = 2;

  private static final void createIndex(MySqlDataStore ds, String tableName, Index index,
      Handler<AsyncResult<String>> handler) {

    checkIndexExists(ds, tableName, index, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        switch (result.result().state) {
        case INDEX_EXISTS:
          handler.handle(Future.succeededFuture("Index exists: " + index.name()));
          break;

        case INDEX_NOT_EXISTS:
          createIndex(ds, tableName, index, result.result(), handler);
          break;

        case INDEX_MODIFIED:
          modifyIndex(ds, tableName, index, result.result(), handler);
          break;

        default:
          handler
              .handle(Future.failedFuture(new UnsupportedOperationException("unsupported result: " + result.result())));
          break;
        }
      }
    });
  }

  private static void createIndex(MySqlDataStore ds, String tableName, Index index, IndexResult res,
      Handler<AsyncResult<String>> handler) {
    handler.handle(Future.failedFuture(new UnsupportedOperationException()));
  }

  private static void modifyIndex(MySqlDataStore ds, String tableName, Index index, IndexResult res,
      Handler<AsyncResult<String>> handler) {
    handler.handle(Future.succeededFuture("Index NOT modified: " + index.name()));
  }

  private static final void checkIndexExists(MySqlDataStore ds, String tableName, Index index,
      Handler<AsyncResult<IndexResult>> handler) {
    String command = "SHOW INDEX FROM " + tableName;
    MySQLClient client = (MySQLClient) ds.getClient();
    query(client, command, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        List<JsonObject> results = result.result().getRows();
        IndexResult res = null;
        for (JsonObject entry : results) {
          String indexName = entry.getString("Key_name");
          if (indexName == null || indexName.hashCode() == 0) {
            handler.handle(Future.failedFuture(new SqlException("Could not determine index name")));
          } else if (indexName.equals(index.name())) {
            res = compare(entry, index);
            break;
          }
        }
        res = res == null ? new IndexResult(INDEX_NOT_EXISTS) : res;
        handler.handle(Future.succeededFuture(res));
      }
    });
  }

  private static IndexResult compare(JsonObject existingIndex, Index indexDef) {
    IndexResult res = new IndexResult(INDEX_EXISTS);
    res.read = existingIndex;
    return res;
  }

  /**
   * Returns true if the type of the {@link IColumnInfo} is numeric
   * 
   * @param col
   *          the {@link IColumnInfo} to be checked
   * @return true, if numeric
   */
  public static boolean isNumeric(IColumnInfo col) {
    return NUMBERIC_TYPES.contains(col.getType().toUpperCase());
  }

  /**
   * Returns true if the type of the {@link IColumnInfo} is character based
   * 
   * @param col
   *          the {@link IColumnInfo} to be checked
   * @return true, if character based
   */
  public static boolean isCharacter(IColumnInfo col) {
    return CHARACTER_TYPES.contains(col.getType().toUpperCase());
  }

  /**
   * Returns true if the type of the {@link IColumnInfo} is date / time based
   * 
   * @param col
   *          the {@link IColumnInfo} to be checked
   * @return true, if date / time based
   */
  public static boolean isDateTime(IColumnInfo col) {
    return DATE_TYPES.contains(col.getType().toUpperCase());
  }

  /**
   * Executes the given query and returns the {@link ResultSet} to the {@link Handler}
   * 
   * @param datastore
   *          the datastore to obtain the connection from
   * @param command
   *          the command to be executed
   * @param resultHandler
   *          a resulthandler to be informed
   */
  public static void query(MySqlDataStore datastore, String command, Handler<AsyncResult<ResultSet>> resultHandler) {
    query((AsyncSQLClient) datastore.getClient(), command, resultHandler);
  }

  /**
   * Executes the given query and returns the {@link ResultSet} to the {@link Handler}
   * 
   * @param sqlClient
   *          the sqlClient to obtain the connection from
   * @param command
   *          the command to be executed
   * @param resultHandler
   *          a resulthandler to be informed
   */
  public static void query(AsyncSQLClient sqlClient, String command, Handler<AsyncResult<ResultSet>> resultHandler) {
    LOGGER.debug("query: " + command);
    sqlClient.getConnection(cr -> {
      if (cr.failed()) {
        Exception sqlEx = new SqlException(ERROR_GAINING_CONNECTION, cr.cause());
        LOGGER.error("", sqlEx);
        resultHandler.handle(Future.failedFuture(sqlEx));
        return;
      }
      SQLConnection connection = cr.result();
      LOGGER.debug(GAINED_SUCCESSFULLY_A_CONNECTION);
      doQuery(command, resultHandler, connection);
    });
  }

  /**
   * @param command
   * @param resultHandler
   * @param connection
   */
  private static void doQuery(String command, Handler<AsyncResult<ResultSet>> resultHandler, SQLConnection connection) {
    connection.query(command, qr -> {
      if (qr.failed()) {
        Exception sqlEx = new SqlException(ERROR_EXECUTING_COMMAND_STATEMENT + command, qr.cause());
        LOGGER.error("", sqlEx);
        connection.close();
        resultHandler.handle(Future.failedFuture(sqlEx));
        return;
      }
      ResultSet res = qr.result();
      LOGGER.debug(COMMAND_SUCCESS);
      connection.close();
      LOGGER.debug(CONNECTION_CLOSED);
      resultHandler.handle(Future.succeededFuture(res));
    });
  }

  /**
   * Executes the given query and returns the {@link ResultSet} to the {@link Handler}
   * 
   * @param datastore
   *          the datastore to obtain the connection from
   * @param command
   *          the command to be executed
   * @param resultHandler
   *          a resulthandler to be informed
   */
  public static void queryWithParams(MySqlDataStore datastore, String command, JsonArray params,
      Handler<AsyncResult<ResultSet>> resultHandler) {
    queryWithParams((AsyncSQLClient) datastore.getClient(), command, params, resultHandler);
  }

  /**
   * Executes the given query and returns the {@link ResultSet} to the {@link Handler}
   * 
   * @param sqlClient
   *          the sqlClient to obtain the connection from
   * @param command
   *          the command to be executed
   * @param resultHandler
   *          a resulthandler to be informed
   */
  public static void queryWithParams(AsyncSQLClient sqlClient, String command, JsonArray params,
      Handler<AsyncResult<ResultSet>> resultHandler) {
    LOGGER.debug("queryWithParams: " + command + " | " + params);
    sqlClient.getConnection(cr -> {
      if (cr.failed()) {
        Exception sqlEx = new SqlException(ERROR_GAINING_CONNECTION, cr.cause());
        LOGGER.error("", sqlEx);
        resultHandler.handle(Future.failedFuture(sqlEx));
        return;
      }
      SQLConnection connection = cr.result();
      LOGGER.debug(GAINED_SUCCESSFULLY_A_CONNECTION);
      doQueryWithParams(command, params, resultHandler, connection);
    });
  }

  /**
   * @param command
   * @param params
   * @param resultHandler
   * @param connection
   */
  private static void doQueryWithParams(String command, JsonArray params, Handler<AsyncResult<ResultSet>> resultHandler,
      SQLConnection connection) {
    connection.queryWithParams(command, params, qr -> {
      if (qr.failed()) {
        Exception sqlEx = new SqlException(ERROR_EXECUTING_COMMAND_STATEMENT + command + " | " + params, qr.cause());
        LOGGER.error("", sqlEx);
        connection.close();
        resultHandler.handle(Future.failedFuture(sqlEx));
        return;
      }
      ResultSet res = qr.result();
      LOGGER.debug(COMMAND_SUCCESS);
      connection.close();
      LOGGER.debug(CONNECTION_CLOSED);
      resultHandler.handle(Future.succeededFuture(res));
    });
  }

  /**
   * Executes the given command and informs the {@link Handler}
   * 
   * @param datastore
   *          the datastore to obtain the connection from
   * @param command
   *          the command to be executed
   * @param resultHandler
   *          a resulthandler to be informed
   */
  public static void execute(MySqlDataStore datastore, String command, Handler<AsyncResult<Void>> resultHandler) {
    execute((AsyncSQLClient) datastore.getClient(), command, resultHandler);
  }

  /**
   * Executes the given command and informs the {@link Handler}
   * 
   * @param sqlClient
   *          the sqlClient to obtain the connection from
   * @param command
   *          the command to be executed
   * @param resultHandler
   *          a resulthandler to be informed
   */
  public static void execute(AsyncSQLClient sqlClient, String command, Handler<AsyncResult<Void>> resultHandler) {
    LOGGER.debug("execute: " + command);
    sqlClient.getConnection(cr -> {
      if (cr.failed()) {
        Exception sqlEx = new SqlException(ERROR_GAINING_CONNECTION, cr.cause());
        LOGGER.error("", sqlEx);
        resultHandler.handle(Future.failedFuture(sqlEx));
        return;
      }

      SQLConnection connection = cr.result();
      LOGGER.debug(GAINED_SUCCESSFULLY_A_CONNECTION);
      doExecute(command, resultHandler, connection);
    });
  }

  /**
   * @param command
   * @param resultHandler
   * @param connection
   */
  private static void doExecute(String command, Handler<AsyncResult<Void>> resultHandler, SQLConnection connection) {
    connection.execute(command, qr -> {
      if (qr.failed()) {
        if (qr.cause().getMessage().contains("doesn't exist")) {
          // nothing to delete, no error
        } else {
          Exception sqlEx = new SqlException(ERROR_EXECUTING_COMMAND_STATEMENT + command, qr.cause());
          LOGGER.error("", sqlEx);
          connection.close();
          resultHandler.handle(Future.failedFuture(sqlEx));
          return;
        }
      }
      LOGGER.debug(COMMAND_SUCCESS);
      connection.close();
      LOGGER.debug(CONNECTION_CLOSED);
      resultHandler.handle(Future.succeededFuture());
    });
  }

  /**
   * Executes the given update command and informs the {@link Handler}
   * 
   * @param datastore
   *          the datastore to obtain the connection from
   * @param command
   *          the command to be executed
   * @param resultHandler
   *          a resulthandler to be informed
   */
  public static void update(MySqlDataStore datastore, String command,
      Handler<AsyncResult<UpdateResult>> resultHandler) {
    update((MySqlDataStore) datastore.getClient(), command, resultHandler);
  }

  /**
   * Executes the given command and informs the {@link Handler}
   * 
   * @param sqlClient
   *          the sqlClient to obtain the connection from
   * @param command
   *          the command to be executed
   * @param resultHandler
   *          a resulthandler to be informed
   */
  public static void update(AsyncSQLClient sqlClient, String command,
      Handler<AsyncResult<UpdateResult>> resultHandler) {
    LOGGER.debug("update: " + command);
    sqlClient.getConnection(cr -> {
      if (cr.failed()) {
        Exception sqlEx = new SqlException(ERROR_GAINING_CONNECTION, cr.cause());
        LOGGER.error("", sqlEx);
        resultHandler.handle(Future.failedFuture(sqlEx));
        return;
      }
      SQLConnection connection = cr.result();
      LOGGER.debug(GAINED_SUCCESSFULLY_A_CONNECTION);
      executeUpdate(connection, command, resultHandler);
    });
  }

  /**
   * Executes the given update command and informs the {@link Handler}
   * 
   * @param datastore
   *          the datastore to obtain the connection from
   * @param command
   *          the command to be executed
   * @param resultHandler
   *          a resulthandler to be informed
   */
  public static void updateWithParams(MySqlDataStore datastore, String command, JsonArray params,
      Handler<AsyncResult<UpdateResult>> resultHandler) {
    updateWithParams((AsyncSQLClient) datastore.getClient(), command, params, resultHandler);
  }

  /**
   * Executes the given command and informs the {@link Handler}
   * 
   * @param sqlClient
   *          the sqlClient to obtain the connection from
   * @param command
   *          the command to be executed
   * @param resultHandler
   *          a resulthandler to be informed
   */
  public static void updateWithParams(AsyncSQLClient sqlClient, String command, JsonArray params,
      Handler<AsyncResult<UpdateResult>> resultHandler) {
    LOGGER.debug("updateWithParams: " + command + " | " + params);
    sqlClient.getConnection(cr -> {
      if (cr.failed()) {
        Exception sqlEx = new SqlException(ERROR_GAINING_CONNECTION, cr.cause());
        LOGGER.error("", sqlEx);
        resultHandler.handle(Future.failedFuture(sqlEx));
      } else {
        SQLConnection connection = cr.result();
        LOGGER.debug(GAINED_SUCCESSFULLY_A_CONNECTION);
        executeUpdateWithParams(connection, command, params, resultHandler);
      }
    });
  }

  private static void executeUpdate(SQLConnection connection, String command,
      Handler<AsyncResult<UpdateResult>> resultHandler) {
    connection.update(command, qr -> {
      if (qr.failed()) {
        Exception sqlEx = new SqlException(ERROR_EXECUTING_COMMAND_STATEMENT + command, qr.cause());
        LOGGER.error("", sqlEx);
        connection.close();
        resultHandler.handle(Future.failedFuture(sqlEx));
        return;
      }
      LOGGER.debug(COMMAND_SUCCESS);
      connection.close();
      LOGGER.debug(CONNECTION_CLOSED);
      resultHandler.handle(Future.succeededFuture(qr.result()));
    });
  }

  private static void executeUpdateWithParams(SQLConnection connection, String command, JsonArray params,
      Handler<AsyncResult<UpdateResult>> resultHandler) {
    connection.updateWithParams(command, params, qr -> {
      if (qr.failed()) {
        Exception sqlEx = new SqlException(ERROR_EXECUTING_COMMAND_STATEMENT + command + " | " + params, qr.cause());
        LOGGER.error("", sqlEx);
        connection.close();
        resultHandler.handle(Future.failedFuture(sqlEx));
      } else {
        LOGGER.debug(COMMAND_SUCCESS);
        connection.close();
        LOGGER.debug(CONNECTION_CLOSED);
        resultHandler.handle(Future.succeededFuture(qr.result()));
      }
    });
  }

  private static class IndexResult {
    short state = -1;
    JsonObject read;

    IndexResult(short state) {
      this.state = state;
    }
  }
}
