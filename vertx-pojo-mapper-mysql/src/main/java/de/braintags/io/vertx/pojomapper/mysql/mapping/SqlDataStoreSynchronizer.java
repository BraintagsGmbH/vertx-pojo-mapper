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

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore;

/**
 * 
 * @author Michael Remme
 * 
 */

public class SqlDataStoreSynchronizer implements IDataStoreSynchronizer {
  private static Logger logger = LoggerFactory.getLogger(SqlDataStoreSynchronizer.class);

  private MySqlDataStore datastore;

  /**
   * 
   */
  public SqlDataStoreSynchronizer(MySqlDataStore ds) {
    this.datastore = ds;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer#synchronize(de.braintags.io.vertx.pojomapper.mapping
   * .IMapper, io.vertx.core.Handler)
   */
  @Override
  public void synchronize(IMapper mapper, Handler<AsyncResult<Void>> resultHandler) {
    readTableFromDatabase(resultHandler);
  }

  private void readTableFromDatabase(Handler<AsyncResult<Void>> resultHandler) {
    // At my sql reading the information schema

    AsyncSQLClient client = datastore.getSqlClient();

    client.getConnection(connectionResult -> {
      if (connectionResult.failed()) {
        logger.error("", connectionResult.cause());
        resultHandler.handle(Future.failedFuture(connectionResult.cause()));
      } else {
        SQLConnection connection = connectionResult.result();
        connection.query("SHOW TABLES", qr -> {
          try {
            if (qr.failed()) {
              logger.error("", qr.cause());
              resultHandler.handle(Future.failedFuture(qr.cause()));
            } else {
              ResultSet res = qr.result();
              logger.info(res);
              resultHandler.handle(Future.succeededFuture());

            }
          } finally {
            logger.info("closing connection - ready");
            connection.close();
          }
        });

      }
    });

    /*
     * 
     * client.getConnection(res -> { if (res.succeeded()) {
     * 
     * SQLConnection connection = res.result();
     * 
     * // Got a connection
     * 
     * } else { // Failed to get connection - deal with it } });
     */

  }
}
