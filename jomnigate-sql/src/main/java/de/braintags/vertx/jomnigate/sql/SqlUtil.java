/*
 * #%L
 * jomnigate-sql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.sql;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import de.braintags.vertx.jomnigate.annotation.Index;
import de.braintags.vertx.jomnigate.mapping.IIndexDefinition;
import de.braintags.vertx.jomnigate.mapping.IIndexFieldDefinition;
import de.braintags.vertx.jomnigate.sql.exception.SqlException;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

/**
 * Utility methods working with {@link SqlDataStore}
 * 
 * @author Michael Remme
 * 
 */
public class SqlUtil {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(SqlUtil.class);
  private static final String ERROR_EXECUTING_COMMAND_STATEMENT = "error executing command. Statement: ";
  private static final String ERROR_GAINING_CONNECTION = "error gaining connection";
  private static final String COMMAND_SUCCESS = "command successful executed";
  private static final String CONNECTION_CLOSED = "connection closed";
  private static final String GEO_INDEX = "CREATE SPATIAL INDEX %s ON %s (%s)";
  public static final short INDEX_EXISTS = 0;
  public static final short INDEX_NOT_EXISTS = 1;
  public static final short INDEX_MODIFIED = 2;

  private SqlUtil() {
  }

  /**
   * Get information about an index with the given name
   * 
   * @param ds
   * @param tableName
   * @param index
   * @return a future which contains the information about the index or null, if none with that name
   */
  public static final Future<JsonObject> getIndexInfo(final SqlDataStore ds, final String tableName,
      final String index) {
    String command = "SHOW INDEX FROM " + tableName;
    Future<JsonObject> f = Future.future();
    query(ds, command).compose(resultSet -> extractIndexInfo(resultSet, index, f), f);
    return f;
  }

  private static void extractIndexInfo(ResultSet rs, String index, Future<JsonObject> f) {
    List<JsonObject> results = rs.getRows();
    JsonObject res = null;
    for (JsonObject entry : results) {
      String indexName = entry.getString("Key_name");
      if (indexName == null || indexName.hashCode() == 0) {
        f.fail(new SqlException("Could not determine index name"));
      } else if (indexName.equals(index)) {
        res = entry;
        break;
      }
    }
    f.complete(res);
  }

  /**
   * Executes the given query and returns the {@link ResultSet} to the {@link Handler}
   * 
   * @param sqlClient
   *          the sqlClient to obtain the connection from
   * @param command
   *          the command to be executed
   * @return a Future whith the ResultSet
   */
  public static Future<ResultSet> query(final SqlDataStore datastore, final String command) {
    LOGGER.debug("query: " + command);
    Future<ResultSet> f = Future.future();
    datastore.getConnection().compose(connection -> doQuery(command, connection, f), f);
    return f;
  }

  /**
   * execute the query with the given connection
   * 
   * @param command
   * @param connection
   * @param f
   *          the future to be informed
   */
  private static void doQuery(final String command, final SQLConnection connection, Future<ResultSet> f) {
    connection.query(command, qr -> {
      if (qr.failed()) {
        Exception sqlEx = new SqlException(ERROR_EXECUTING_COMMAND_STATEMENT + command, qr.cause());
        LOGGER.error("", sqlEx);
        connection.close();
        f.fail(sqlEx);
      } else {
        ResultSet res = qr.result();
        LOGGER.debug(COMMAND_SUCCESS);
        connection.close();
        LOGGER.debug(CONNECTION_CLOSED);
        f.complete(res);
      }
    });
  }

  /**
   * execute the given command
   * 
   * @param datastore
   * @param command
   * @return
   */
  public static Future<Void> executeCommand(final SqlDataStore datastore, final String command) {
    Future<Void> f = Future.future();
    executeCommand(datastore, command, f);
    return f;
  }

  /**
   * execute the given command
   * 
   * @param datastore
   * @param command
   * @param f
   *          the future to be informed
   */
  public static void executeCommand(final SqlDataStore datastore, final String command, Future<Void> f) {
    datastore.getConnection().compose(connection -> doExecute(command, connection, f), f);
  }

  /**
   * execute the command with the given connection
   * 
   * @param command
   * @param connection
   * @param f
   *          the future to be informed
   */
  private static void doExecute(final String command, final SQLConnection connection, Future<Void> f) {
    connection.execute(command, qr -> {
      if (qr.failed()) {
        if (qr.cause().getMessage().contains("doesn't exist")) {
          // nothing to delete, no error
        } else {
          Exception sqlEx = new SqlException(ERROR_EXECUTING_COMMAND_STATEMENT + command, qr.cause());
          LOGGER.error("", sqlEx);
          connection.close();
          f.fail(sqlEx);
        }
      }
      LOGGER.debug(COMMAND_SUCCESS);
      connection.close();
      LOGGER.debug(CONNECTION_CLOSED);
      f.complete();
    });
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
  public static final void createIndexes(final SqlDataStore ds, final String tableName,
      final ImmutableSet<IIndexDefinition> indexDefinitions, final Handler<AsyncResult<String>> handler) {
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
  private static List<Future> createFutureList(final SqlDataStore ds, final String tableName,
      final ImmutableSet<IIndexDefinition> indexDefinitions) {
    List<Future> fl = new ArrayList<>();
    for (IIndexDefinition index : indexDefinitions) {
      fl.add(createIndex(ds, tableName, index));
    }
    return fl;
  }

  private static final Future<Void> createIndex(final SqlDataStore ds, final String tableName,
      final IIndexDefinition index) {
    Future<Void> f = Future.future();
    Future<IndexResult> ir = checkIndexExists(ds, tableName, index);
    ir.setHandler(result -> {
      if (result.failed()) {
        f.fail(result.cause());
      } else {
        switch (result.result().state) {
        case INDEX_EXISTS:
          LOGGER.debug("Index exists: " + index.getName());
          f.complete();
          break;
        case INDEX_NOT_EXISTS:
          createIndex(ds, tableName, index, f);
          break;
        case INDEX_MODIFIED:
          modifyIndex(ds, tableName, index, result.result(), f);
          break;
        default:
          f.fail(new UnsupportedOperationException("unsupported result: " + result.result()));
          break;
        }
      }
    });

    return f;
  }

  private static void createIndex(final SqlDataStore ds, final String tableName, final IIndexDefinition index,
      Future<Void> f) {
    List<IIndexFieldDefinition> fields = index.getFields();
    if (fields.size() > 1) {
      f.fail(new UnsupportedOperationException("Not yet supported: more than one field in index"));
    }
    for (IIndexFieldDefinition field : fields) {
      switch (field.getType()) {
      case GEO2DSPHERE:
      case GEO2D:
        createGeoIndex(ds, index.getName(), tableName, field, f);
        break;

      default:
        f.fail(new UnsupportedOperationException("Index type not supported: " + field.getType()));
      }
    }

  }

  private static void createGeoIndex(final SqlDataStore ds, final String indexName, final String tableName,
      final IIndexFieldDefinition field, Future<Void> f) {
    String createString = String.format(GEO_INDEX, indexName, tableName, field.getName());
    executeCommand(ds, createString, f);
  }

  private static void modifyIndex(final SqlDataStore ds, final String tableName, final IIndexDefinition index,
      final IndexResult res, final Future<Void> f) {
    f.fail(
        new UnsupportedOperationException("Indexdefinition is modified and should be adapted in the database for table "
            + tableName + " | " + index.getName()));
  }

  private static final Future<IndexResult> checkIndexExists(final SqlDataStore ds, final String tableName,
      final IIndexDefinition index) {
    Future<IndexResult> returnFuture = Future.future();
    Future<JsonObject> f = getIndexInfo(ds, tableName, index.getName());
    f.setHandler(result -> {
      if (result.failed()) {
        returnFuture.fail(result.cause());
      } else {
        IndexResult res = null;
        JsonObject entry = result.result();
        if (entry == null) {
          res = new IndexResult(INDEX_NOT_EXISTS);
        } else {
          res = compare(entry, index);
        }
        returnFuture.complete(res);
      }
    });
    return returnFuture;
  }

  private static IndexResult compare(final JsonObject existingIndex, final IIndexDefinition index) {
    IndexResult res = new IndexResult(INDEX_EXISTS);
    res.read = existingIndex;
    return res;
  }

  private static class IndexResult {
    short state = -1;
    JsonObject read;

    IndexResult(final short state) {
      this.state = state;
    }
  }
}
