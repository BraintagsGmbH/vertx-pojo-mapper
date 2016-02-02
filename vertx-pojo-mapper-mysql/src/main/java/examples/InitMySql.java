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
package examples;

import java.util.Objects;

import de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.impl.keygen.DefaultKeyGenerator;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.docgen.Source;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;

@Source(translate = false)
public class InitMySql {
  private MySqlDataStore datastore;
  private AsyncSQLClient mySQLClient;

  public void initMySqlClient(Vertx vertx, String username, String password, String database) {
    Objects.requireNonNull(username, "Username is required");
    Objects.requireNonNull(password, "Password is required");
    Objects.requireNonNull(database, "database is required");

    JsonObject mySQLClientConfig = new JsonObject().put("host", "localhost").put("username", username)
        .put("password", password).put("database", database).put("port", 3306)
        .put(IKeyGenerator.DEFAULT_KEY_GENERATOR, DefaultKeyGenerator.NAME);

    mySQLClient = MySQLClient.createShared(vertx, mySQLClientConfig);
    datastore = new MySqlDataStore(vertx, mySQLClient, mySQLClientConfig);
  }
}
