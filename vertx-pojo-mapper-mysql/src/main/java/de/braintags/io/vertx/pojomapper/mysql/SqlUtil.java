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

import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mysql.exception.SqlException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.asyncsql.AsyncSQLClient;
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
    query(datastore.getSqlClient(), command, resultHandler);
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
    queryWithParams(datastore.getSqlClient(), command, params, resultHandler);
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
    execute(datastore.getSqlClient(), command, resultHandler);
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
    update(datastore.getSqlClient(), command, resultHandler);
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
    updateWithParams(datastore.getSqlClient(), command, params, resultHandler);
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
}
