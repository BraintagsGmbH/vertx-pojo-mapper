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

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.datastoretest.IDatastoreContainer;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.AbstractTypeHandlerTest;
import de.braintags.io.vertx.pojomapper.exception.ParameterRequiredException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;

/**
 * 
 * @author Michael Remme
 * 
 */

public class MySqlDataStoreContainer implements IDatastoreContainer {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(MySqlDataStoreContainer.class);

  private MySqlDataStore datastore;
  private AsyncSQLClient mySQLClient;

  /**
   * 
   */
  public MySqlDataStoreContainer() {
  }

  @Override
  public void startup(Vertx vertx, Handler<AsyncResult<Void>> handler) {
    try {
      String username = System.getProperty("MySqlDataStoreContainer.username", null);
      if (username == null) {
        throw new ParameterRequiredException("you must set the property 'MySqlDataStoreContainer.username'");
      }
      String password = System.getProperty("MySqlDataStoreContainer.password", null);
      if (password == null) {
        throw new ParameterRequiredException("you must set the property 'MySqlDataStoreContainer.password'");
      }

      String database = "test";
      JsonObject mySQLClientConfig = new JsonObject().put("host", "localhost").put("username", username)
          .put("password", password).put("database", database).put("port", 3306).put("initial_pool_size", 10);
      mySQLClient = MySQLClient.createShared(vertx, mySQLClientConfig);
      datastore = new MySqlDataStore(mySQLClient, database);
      handler.handle(Future.succeededFuture());
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.test.IDatastoreContainer#getDataStore()
   */
  @Override
  public IDataStore getDataStore() {
    return datastore;
  }

  @Override
  public void shutdown(Handler<AsyncResult<Void>> handler) {
    mySQLClient.close(handler);
  }

  @Override
  public void dropTable(String tableName, Handler<AsyncResult<Void>> handler) {
    String command = "DROP TABLE IF EXISTS " + tableName;
    SqlUtil.execute(datastore, command, dr -> {
      if (dr.failed()) {
        LOGGER.error("error deleting table", dr.cause());
        handler.handle(Future.failedFuture(dr.cause()));
        return;
      }
      LOGGER.info("Deleted table " + tableName);
      handler.handle(Future.succeededFuture());
    });
  }

  @Override
  public String getExpectedTypehandlerName(Class<? extends AbstractTypeHandlerTest> testClass, String defaultName) {
    return defaultName;
  }

}
