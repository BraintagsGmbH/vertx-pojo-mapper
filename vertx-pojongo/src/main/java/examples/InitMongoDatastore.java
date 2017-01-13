/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package examples;

import java.util.Objects;

import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.docgen.Source;
import io.vertx.ext.mongo.MongoClient;

@Source(translate = false)
public class InitMongoDatastore {

  public void initMongoDatastore(Vertx vertx, String database) {
    Objects.requireNonNull(database, "database is required");
    JsonObject config = new JsonObject();
    config.put("connection_string", "mongodb://localhost:27017");
    config.put("db_name", database);
    MongoClient mongoClient = MongoClient.createNonShared(vertx, config);
    MongoDataStore mongoDataStore = new MongoDataStore(vertx, mongoClient, config);
  }
}
