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

import de.braintags.vertx.jomnigate.sql.exception.SqlException;
import io.vertx.core.Future;
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
