/*
 * #%L
 * jomnigate-mysql
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

import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.sql.SqlDataStore;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.SQLConnection;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MySqlDataStore extends SqlDataStore {
  private AsyncSQLClient sqlClient;

  /**
   * @param vertx
   * @param properties
   * @param settings
   */
  public MySqlDataStore(Vertx vertx, AsyncSQLClient sqlClient, JsonObject properties, DataStoreSettings settings) {
    super(vertx, properties, settings);
    this.sqlClient = sqlClient;
  }

  @Override
  public Future<SQLConnection> getConnection() {
    Future<SQLConnection> f = Future.future();
    sqlClient.getConnection(f);
    return f;
  }

}
