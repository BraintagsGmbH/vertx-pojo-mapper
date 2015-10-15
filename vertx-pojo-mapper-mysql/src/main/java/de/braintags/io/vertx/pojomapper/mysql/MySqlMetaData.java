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

import de.braintags.io.vertx.pojomapper.IDataStoreMetaData;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.SQLConnection;

/**
 * Meta information about the connected database
 * 
 * @author Michael Remme
 * 
 */

public class MySqlMetaData implements IDataStoreMetaData {
  private AsyncSQLClient sqlClient;
  private String version;

  /**
   * Constructor for the Metadata
   * 
   * @param sqlClient
   *          the client to be used
   */
  public MySqlMetaData(AsyncSQLClient sqlClient) {
    this.sqlClient = sqlClient;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStoreMetaData#getVersion(io.vertx.core.Handler)
   */
  @Override
  public void getVersion(Handler<AsyncResult<String>> handler) {
    if (version != null) {
      handler.handle(Future.succeededFuture(version));
      return;
    }
    sqlClient.getConnection(conn -> {
      if (conn.failed()) {
        handler.handle(Future.failedFuture(conn.cause()));
      } else {
        SQLConnection connection = conn.result();
        connection.query("SELECT VERSION()", result -> {
          try {
            if (result.failed()) {
              handler.handle(Future.failedFuture(conn.cause()));
            } else {
              version = result.result().getResults().get(0).getString(0);
              handler.handle(Future.succeededFuture(version));
            }
          } finally {
            connection.close();
          }
        });
      }
    });

  }

}
