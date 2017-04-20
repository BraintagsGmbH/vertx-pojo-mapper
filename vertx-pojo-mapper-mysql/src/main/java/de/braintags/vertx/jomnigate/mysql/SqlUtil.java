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

package de.braintags.vertx.jomnigate.mysql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.mauricio.async.db.mysql.exceptions.MySQLException;
import com.google.common.collect.ImmutableSet;

import de.braintags.vertx.jomnigate.annotation.Index;
import de.braintags.vertx.jomnigate.exception.DuplicateKeyException;
import de.braintags.vertx.jomnigate.mapping.IIndexDefinition;
import de.braintags.vertx.jomnigate.mapping.IIndexFieldDefinition;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo;
import de.braintags.vertx.jomnigate.mysql.exception.SqlException;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
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
  private static final String GEO_INDEX = "CREATE SPATIAL INDEX %s ON %s (%s)";
  public static final short INDEX_EXISTS = 0;
  public static final short INDEX_NOT_EXISTS = 1;
  public static final short INDEX_MODIFIED = 2;

  private SqlUtil() {
  }

  /**
   * Create indexes which are defined by the given {@link Index}
   * 
   * @param ds
   *          the datastore
   * @param collection
   *          the name of the collection to be used
   * @param indexDefinitions
   *          the index definition
   * @param handler
   *          the handler to be informed
   */
  public static final void createIndexes(final MySqlDataStore ds, final String tableName,
      final ImmutableSet<IIndexDefinition> indexDefinitions,
      final Handler<AsyncResult<String>> handler) {
    if (indexDefinitions == null || indexDefinitions.isEmpty()) {
      handler.handle(Future.succeededFuture("No indexes defined"));
    } else {
      List<Future> fl = createFutureList(ds, tableName, indexDefinitions);
      Buffer returnBuffer = Buffer.buffer();
      CompositeFuture cf = CompositeFuture.all(fl);
      cf.setHandler(result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          cf.list().forEach(f -> returnBuffer.appendString((String) f));
          handler.handle(Future.succeededFuture(returnBuffer.toString()));
        }
      });
    }
  }

  @SuppressWarnings("rawtypes")
  private static List<Future> createFutureList(final MySqlDataStore ds, final String tableName,
      final ImmutableSet<IIndexDefinition> indexDefinitions) {
    List<Future> fl = new ArrayList<>();
    for (IIndexDefinition index : indexDefinitions) {
      fl.add(createIndex(ds, tableName, index));
    }
    return fl;
  }

  private static final Future<String> createIndex(final MySqlDataStore ds, final String tableName,
      final IIndexDefinition index) {
    Future<String> f = Future.future();
    checkIndexExists(ds, tableName, index, result -> {
      if (result.failed()) {
        f.fail(result.cause());
      } else {
        switch (result.result().state) {
        case INDEX_EXISTS:
          f.complete("Index exists: " + index.getName());
          break;
        case INDEX_NOT_EXISTS:
          createIndex(ds, tableName, index, f.completer());
          break;
        case INDEX_MODIFIED:
          modifyIndex(ds, tableName, index, result.result(), f.completer());
          break;
        default:
          f.fail(new UnsupportedOperationException("unsupported result: " + result.result()));
          break;
        }
      }
    });
    return f;
  }

  private static void createIndex(final MySqlDataStore ds, final String tableName, final IIndexDefinition index,
      final Handler<AsyncResult<String>> handler) {
    List<IIndexFieldDefinition> fields = index.getFields();
    if (fields.size() > 1) {
      handler.handle(
          Future.failedFuture(new UnsupportedOperationException("Not yet supported: more than one field in index")));
      return;
    }
    for (IIndexFieldDefinition field : fields) {
      switch (field.getType()) {
      case GEO2DSPHERE:
      case GEO2D:
        createGeoIndex(ds, index.getName(), tableName, field, handler);
        break;

      default:
        handler.handle(
            Future.failedFuture(new UnsupportedOperationException("Index type not supported: " + field.getType())));
      }
    }

  }

  private static void createGeoIndex(final MySqlDataStore ds, final String indexName, final String tableName,
      final IIndexFieldDefinition field,
      final Handler<AsyncResult<String>> handler) {
    String createString = String.format(GEO_INDEX, indexName, tableName, field.getName());
    execute(ds, createString, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture("Index successfully created: " + createString));
      }
    });
  }

  private static void modifyIndex(final MySqlDataStore ds, final String tableName, final IIndexDefinition index,
      final IndexResult res,
      final Handler<AsyncResult<String>> handler) {
    handler.handle(Future.succeededFuture("Indexdefinition is modified and should be adapted in the database for table "
        + tableName + " | " + index.getName()));
  }

  /**
   * Get information about an index with the given name
   * 
   * @param ds
   * @param tableName
   * @param index
   * @param handler
   *          handler to be informed about existing index or null, if none with that name
   */
  public static final void getIndexInfo(final MySqlDataStore ds, final String tableName, final String index,
      final Handler<AsyncResult<JsonObject>> handler) {
    String command = "SHOW INDEX FROM " + tableName;
    AsyncSQLClient client = (AsyncSQLClient) ds.getClient();
    query(client, command, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        List<JsonObject> results = result.result().getRows();
        JsonObject res = null;
        for (JsonObject entry : results) {
          String indexName = entry.getString("Key_name");
          if (indexName == null || indexName.hashCode() == 0) {
            handler.handle(Future.failedFuture(new SqlException("Could not determine index name")));
          } else if (indexName.equals(index)) {
            res = entry;
            break;
          }
        }
        handler.handle(Future.succeededFuture(res));
      }
    });
  }

  private static final void checkIndexExists(final MySqlDataStore ds, final String tableName,
      final IIndexDefinition index,
      final Handler<AsyncResult<IndexResult>> handler) {
    getIndexInfo(ds, tableName, index.getName(), result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        IndexResult res = null;
        JsonObject entry = result.result();
        if (entry == null) {
          res = new IndexResult(INDEX_NOT_EXISTS);
        } else {
          res = compare(entry, index);
        }
        handler.handle(Future.succeededFuture(res));
      }
    });

  }

  private static IndexResult compare(final JsonObject existingIndex, final IIndexDefinition index) {
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
  public static boolean isNumeric(final IColumnInfo col) {
    return NUMBERIC_TYPES.contains(col.getType().toUpperCase());
  }

  /**
   * Returns true if the type of the {@link IColumnInfo} is character based
   * 
   * @param col
   *          the {@link IColumnInfo} to be checked
   * @return true, if character based
   */
  public static boolean isCharacter(final IColumnInfo col) {
    return CHARACTER_TYPES.contains(col.getType().toUpperCase());
  }

  /**
   * Returns true if the type of the {@link IColumnInfo} is date / time based
   * 
   * @param col
   *          the {@link IColumnInfo} to be checked
   * @return true, if date / time based
   */
  public static boolean isDateTime(final IColumnInfo col) {
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
  public static void query(final MySqlDataStore datastore, final String command, final Handler<AsyncResult<ResultSet>> resultHandler) {
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
  public static void query(final AsyncSQLClient sqlClient, final String command, final Handler<AsyncResult<ResultSet>> resultHandler) {
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
  private static void doQuery(final String command, final Handler<AsyncResult<ResultSet>> resultHandler, final SQLConnection connection) {
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
  public static void queryWithParams(final MySqlDataStore datastore, final String command, final JsonArray params,
      final Handler<AsyncResult<ResultSet>> resultHandler) {
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
  public static void queryWithParams(final AsyncSQLClient sqlClient, final String command, final JsonArray params,
      final Handler<AsyncResult<ResultSet>> resultHandler) {
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
  private static void doQueryWithParams(final String command, final JsonArray params, final Handler<AsyncResult<ResultSet>> resultHandler,
      final SQLConnection connection) {
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
  public static void execute(final MySqlDataStore datastore, final String command, final Handler<AsyncResult<Void>> resultHandler) {
    if (datastore == null)
      throw new NullPointerException("datastore is null");
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
  public static void execute(final AsyncSQLClient sqlClient, final String command, final Handler<AsyncResult<Void>> resultHandler) {
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
  private static void doExecute(final String command, final Handler<AsyncResult<Void>> resultHandler, final SQLConnection connection) {
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
  public static void update(final MySqlDataStore datastore, final String command,
      final Handler<AsyncResult<UpdateResult>> resultHandler) {
    update((AsyncSQLClient) datastore.getClient(), command, resultHandler);
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
  public static void update(final AsyncSQLClient sqlClient, final String command,
      final Handler<AsyncResult<UpdateResult>> resultHandler) {
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
  public static void updateWithParams(final MySqlDataStore datastore, final String command, final JsonArray params,
      final Handler<AsyncResult<UpdateResult>> resultHandler) {
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
  public static void updateWithParams(final AsyncSQLClient sqlClient, final String command, final JsonArray params,
      final Handler<AsyncResult<UpdateResult>> resultHandler) {
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

  private static void executeUpdate(final SQLConnection connection, final String command,
      final Handler<AsyncResult<UpdateResult>> resultHandler) {
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

  private static void executeUpdateWithParams(final SQLConnection connection, final String command, final JsonArray params,
      final Handler<AsyncResult<UpdateResult>> resultHandler) {
    connection.updateWithParams(command, params, qr -> {
      if (qr.failed()) {
        connection.close();
        Throwable error = qr.cause();
        if (error instanceof MySQLException && error.getMessage().indexOf("Duplicate entry") >= 0
            && error.getMessage().indexOf("for key 'PRIMARY'") >= 0) {
          resultHandler.handle(Future.failedFuture(new DuplicateKeyException(error)));
        } else {
          Exception sqlEx = new SqlException(ERROR_EXECUTING_COMMAND_STATEMENT + command + " | " + params, error);
          resultHandler.handle(Future.failedFuture(sqlEx));
        }
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

    IndexResult(final short state) {
      this.state = state;
    }
  }
}
