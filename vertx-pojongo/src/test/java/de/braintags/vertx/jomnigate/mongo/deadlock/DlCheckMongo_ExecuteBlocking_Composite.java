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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.braintags.vertx.util.ResultObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class DlCheckMongo_ExecuteBlocking_Composite {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(DlCheckMongo_ExecuteBlocking_Composite.class);

  private Vertx vertx = Vertx.vertx();
  private MongoClient mongoClient;

  /**
   * 
   */
  public DlCheckMongo_ExecuteBlocking_Composite(boolean shared) {
    initMongoClient(shared);
  }

  private void initMongoClient(boolean shared) {
    JsonObject jconfig = createConfig();
    mongoClient = shared ? MongoClient.createShared(vertx, jconfig) : MongoClient.createNonShared(vertx, jconfig);
  }

  protected JsonObject createConfig() {
    JsonObject config = new JsonObject();
    config.put("connection_string", "mongodb://localhost:27017");
    config.put("db_name", "deadlockDb");
    return config;
  }

  public void execute() {
    String COLLECTION = "massInsert";
    CountDownLatch latch = new CountDownLatch(1);

    FindOptions fo = new FindOptions().setLimit(5000);
    ResultObject<List<JsonObject>> ro = new ResultObject(null);
    mongoClient.findWithOptions(COLLECTION, new JsonObject(), fo, result -> {
      try {
        System.out.println(result.result().size());
        ro.setResult(handleResult(mongoClient, result));
      } catch (Throwable e) {
        ro.setThrowable(e);
      }
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

  private List<JsonObject> handleResult(MongoClient client, AsyncResult<List<JsonObject>> seachResult)
      throws Throwable {
    CountDownLatch latch = new CountDownLatch(1);
    ResultObject<List<JsonObject>> ro = new ResultObject(null);
    List<Future> fl = new ArrayList<>();

    Future<List<JsonObject>> f = Future.future();
    vertx.executeBlocking(fb -> {
      executeQuery(client, fb.completer());
    }, f.completer());

    f.setHandler(result -> {
      if (result.failed()) {
        LOGGER.error("error occured", result.cause());
        ro.setThrowable(result.cause());
      } else {
        LOGGER.info("created content as " + result.result());
        ro.setResult(result.result());
      }
      latch.countDown();
    });

    latch.await();
    if (ro.isError()) {
      throw ro.getRuntimeException();
    } else {
      return ro.getResult();
    }
  }

  private void executeQuery(MongoClient client, Handler<AsyncResult<List<JsonObject>>> handler) {
    FindOptions fo = new FindOptions().setLimit(5000);
    client.findWithOptions("MiniMapper", new JsonObject(), fo, result -> {
      System.out.println("query executed");
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture(result.result()));
      }
    });
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    DlCheckMongo_ExecuteBlocking_Composite main = new DlCheckMongo_ExecuteBlocking_Composite(true);
    main.execute();

  }

}
