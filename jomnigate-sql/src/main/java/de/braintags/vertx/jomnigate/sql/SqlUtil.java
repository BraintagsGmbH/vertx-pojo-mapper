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

import java.util.List;

import de.braintags.vertx.jomnigate.sql.exception.SqlException;
import io.vertx.core.Future;
import io.vertx.core.Handler;
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
    query(ds, command).compose(resultSet -> extractIndexInfo(resultSet, index), f);
    return f;
  }

  private static Future<JsonObject> extractIndexInfo(ResultSet rs, String index) {
    Future<JsonObject> f = Future.future();
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
    return f;
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
    datastore.getConnection().compose(connection -> doQuery(command, connection), f);
    return f;
  }

  /**
   * @param command
   * @param resultHandler
   * @param connection
   */
  private static Future<ResultSet> doQuery(final String command, final SQLConnection connection) {
    Future<ResultSet> f = Future.future();
    connection.query(command, qr -> {
      if (qr.failed()) {
        Exception sqlEx = new SqlException(ERROR_EXECUTING_COMMAND_STATEMENT + command, qr.cause());
        LOGGER.error("", sqlEx);
        connection.close();
        f.fail(sqlEx);
      }
      ResultSet res = qr.result();
      LOGGER.debug(COMMAND_SUCCESS);
      connection.close();
      LOGGER.debug(CONNECTION_CLOSED);
      f.complete(res);
    });
    return f;
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
    datastore.getConnection().compose(connection -> doExecute(command, connection), f);
    return f;
  }

  /**
   * @param command
   * @param resultHandler
   * @param connection
   */
  private static Future<Void> doExecute(final String command, final SQLConnection connection) {
    Future<Void> f = Future.future();
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
    return f;
  }

}
