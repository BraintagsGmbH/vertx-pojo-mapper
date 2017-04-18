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
package de.braintags.vertx.jomnigate.mongo.deadlock;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.braintags.vertx.util.ResultObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;

/**
 * How to execute a blocking query inside a blocking query?
 * 
 * @author Michael Remme
 * 
 */
public class DlCheckMongo_ExecuteBlocking {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(DlCheckMongo_ExecuteBlocking.class);

  private Vertx vertx = Vertx.vertx();
  private static MongoClient mongoClient;

  /**
   * 
   */
  public DlCheckMongo_ExecuteBlocking(boolean shared) {
    initMongoClient(shared);
  }

  private void initMongoClient(boolean shared) {
    JsonObject config = new JsonObject();
    config.put("connection_string", "mongodb://localhost:27017");
    config.put("db_name", "deadlockDb");
    mongoClient = shared ? MongoClient.createShared(vertx, config) : MongoClient.createNonShared(vertx, config);
  }

  public void execute() {
    System.out.println("STARTING EXECUTION: Thread: " + Thread.currentThread().getName());
    String COLLECTION = "massInsert";
    ResultObject<List<JsonObject>> ro = new ResultObject(null);
    CountDownLatch latch = new CountDownLatch(1);

    vertx.<List<JsonObject>> executeBlocking(future -> {
      System.out.println("EXECUTE BLOCKING1: Thread: " + Thread.currentThread().getName());
      FindOptions fo = new FindOptions().setLimit(5000);
      mongoClient.findWithOptions(COLLECTION, new JsonObject(), fo, result -> {
        try {
          System.out.println("RESULT SIZE IS: " + result.result().size());
          List<JsonObject> jList = executeBlocking2(mongoClient, result);
          System.out.println("HANDLE RESULT SIZE IS: " + jList.size());
          future.complete(jList);
        } catch (Throwable e) {
          future.fail(e);
        }
      });
    }, false, result -> {
      if (result.failed()) {
        ro.setThrowable(result.cause());
      } else {
        ro.setResult(result.result());
      }
      System.out.println("finishing first");
      latch.countDown();
    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    if (ro.isError()) {
      ro.getThrowable().printStackTrace();
    } else {
      System.out.println("finished");
    }
    vertx.close();
  }

  private List<JsonObject> executeBlocking2(MongoClient client, AsyncResult<List<JsonObject>> seachResult)
      throws Throwable {
    CountDownLatch latch = new CountDownLatch(1);
    ResultObject<List<JsonObject>> ro = new ResultObject(null);

    vertx.<List<JsonObject>> executeBlocking(future -> {
      System.out.println("EXECUTE BLOCKING2: Thread: " + Thread.currentThread().getName());
      future.complete();
      // executeQuery(client, fb);
    }, false, res -> {
      // all done already
      System.out.println("got it");
      latch.countDown();
    });

    latch.await();
    if (ro.isError()) {
      throw ro.getRuntimeException();
    } else {
      return ro.getResult();
    }
  }

  private void executeQuery(MongoClient client, Future f) {
    FindOptions fo = new FindOptions().setLimit(5000);
    client.findWithOptions("MiniMapper", new JsonObject(), fo, f);
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    DlCheckMongo_ExecuteBlocking main = new DlCheckMongo_ExecuteBlocking(true);
    main.execute();

  }

}
