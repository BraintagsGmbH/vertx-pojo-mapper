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

import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mongo.MongoDataStore;
import de.braintags.vertx.jomnigate.mongo.performance.mapper.Animal;
import de.braintags.vertx.jomnigate.mongo.performance.mapper.PersonWithAnimal_List;
import de.braintags.vertx.jomnigate.testdatastore.DatastoreBaseTest;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * perform createStoreObject 50 times 50.000 instances:
 * 
 * with typehandlers active: 2267 ms, 2230 ms, 2216 ms
 * 
 * only objecttypehandler active: 1519 ms, 1545 ms, 1493 ms
 * 
 * 
 * 
 * @author Michael Remme
 * 
 */
public class PerfMappingPersonAnimal_List extends DatastoreBaseTest {
  private static final int LOOP = 50000;

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public synchronized static void start(MongoDataStore ds, AtomicLong al) {
    final Long startTime = System.currentTimeMillis();
    IMapper<PersonWithAnimal_List> mapper = ds.getMapperFactory().getMapper(PersonWithAnimal_List.class);
    List<Future> fl = new ArrayList<>();
    for (int i = 0; i < LOOP; i++) {
      Future f = Future.future();
      fl.add(f);
      ds.getMapperFactory().getStoreObjectFactory().createStoreObject(mapper, new PersonWithAnimal_List(i),
          f.completer());
    }

    CompositeFuture cf = CompositeFuture.all(fl);
    cf.setHandler(res -> {
      if (res.failed()) {
        res.cause().printStackTrace();
      } else {
        long t = System.currentTimeMillis() - startTime;
        System.out.println(t);
        al.addAndGet(t);
      }
    });
  }

  public static void main(String[] args) {
    int loops = 50;
    Vertx vertx = Vertx.vertx();
    JsonObject config = new JsonObject();
    config.put("connection_string", "mongodb://localhost:27017");
    config.put("db_name", "PojongoTestDatabase");
    AtomicLong allTime = new AtomicLong();
    MongoClient mongoClient = MongoClient.createNonShared(vertx, config);
    MongoDataStore store = new MongoDataStore(vertx, mongoClient, config);
    store.getMapperFactory().getMapper(PersonWithAnimal_List.class);
    store.getMapperFactory().getMapper(Animal.class);

    for (int i = 0; i < loops; i++) {
      start(store, allTime);
    }
    System.out.println("average: " + allTime.get() / loops);
  }

}
