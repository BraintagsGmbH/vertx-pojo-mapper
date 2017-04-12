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
package de.braintags.vertx.jomnigate.mongo.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.databind.JsonNode;

import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mongo.MongoDataStore;
import de.braintags.vertx.jomnigate.mongo.performance.mapper.PersonPure;
import de.braintags.vertx.jomnigate.testdatastore.DatastoreBaseTest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * just perform jackson serialize 50 times 50.000 instances:
 * 
 * runs: 581 ms, 564 ms, 600 ms
 * 
 * 
 * 
 * @author Michael Remme
 * 
 */
public class PerfMappingPersonPure_Jackson extends DatastoreBaseTest {
  private static final int LOOP = 50000;

  @SuppressWarnings({ "rawtypes", "unused" })
  public synchronized static void start(MongoDataStore ds, AtomicLong al) throws Exception {
    final Long startTime = System.currentTimeMillis();
    IMapper<PersonPure> mapper = ds.getMapperFactory().getMapper(PersonPure.class);
    List<Future> fl = new ArrayList<>();
    for (int i = 0; i < LOOP; i++) {
      JsonNode jsonNode = Json.mapper.valueToTree(new PersonPure(i));
      JsonObject result = new JsonObject(Json.mapper.writeValueAsString(jsonNode));
    }
    long t = System.currentTimeMillis() - startTime;
    System.out.println(t);
    al.addAndGet(t);

  }

  public static void main(String[] args) throws Exception {
    int loops = 50;
    Vertx vertx = Vertx.vertx();
    JsonObject config = new JsonObject();
    config.put("connection_string", "mongodb://localhost:27017");
    config.put("db_name", "PojongoTestDatabase");
    AtomicLong allTime = new AtomicLong();
    MongoClient mongoClient = MongoClient.createNonShared(vertx, config);
    MongoDataStore store = new MongoDataStore(vertx, mongoClient, config, null);
    store.getMapperFactory().getMapper(PersonPure.class);

    for (int i = 0; i < loops; i++) {
      start(store, allTime);
    }
    System.out.println("average: " + allTime.get() / loops);
  }

}
