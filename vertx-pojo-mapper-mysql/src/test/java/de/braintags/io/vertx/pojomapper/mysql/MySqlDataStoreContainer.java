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

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.exception.ParameterRequiredException;
import de.braintags.io.vertx.pojomapper.test.IDatastoreContainer;

/**
 * 
 * @author Michael Remme
 * 
 */

public class MySqlDataStoreContainer implements IDatastoreContainer {
  private MySqlDataStore datastore;
  private AsyncSQLClient mySQLClient;

  /**
   * 
   */
  public MySqlDataStoreContainer() {
  }

  @Override
  public void startup(Vertx vertx) {
    String username = System.getProperty("username");
    if (username == null) {
      throw new ParameterRequiredException("you must set the property 'username'");
    }
    String password = System.getProperty("password");
    if (password == null) {
      throw new ParameterRequiredException("you must set the property 'password'");
    }

    String database = "test";
    JsonObject mySQLClientConfig = new JsonObject().put("host", "localhost").put("username", username)
        .put("password", password).put("database", database).put("port", 3306);
    mySQLClient = MySQLClient.createShared(vertx, mySQLClientConfig);
    datastore = new MySqlDataStore(mySQLClient, database);
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
  public void shutdown() {
    mySQLClient.close();
  }

}
