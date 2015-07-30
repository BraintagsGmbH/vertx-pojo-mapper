/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.mongo;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.dataaccess.write.impl.WriteResult;
import de.braintags.io.vertx.pojomapper.mongo.test.MongoBaseTest;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class MongoBulkTest extends MongoBaseTest {
  private static Logger logger = LoggerFactory.getLogger(MongoBulkTest.class);
  private static final long LOOP = 10000;
  private static final String COLUMN = "BulkTest";

  @BeforeClass
  public static void beforeClass() throws Exception {
    System.setProperty("connection_string", "mongodb://localhost:27017");
    System.setProperty("db_name", "PojongoTestDatabase");
    MongoBaseTest.startMongo();
  }

  @AfterClass
  public static void afterClass() {
    MongoBaseTest.stopMongo();
  }

  /**
   * 
   */
  public MongoBulkTest() {
  }

  @Test
  public void BulkTest() {
    CountDownLatch latch = new CountDownLatch(1);
    List<JsonObject> list = createRecords();
    ErrorObject ro = new ErrorObject();
    doSaveList(list, result -> {
      if (result.failed()) {
        logger.error("", result.cause());
        latch.countDown();
      } else {
        checkResult(result.result(), cr -> {
          if (cr.failed()) {
            logger.error("", cr.cause());
            ro.setThrowable(cr.cause());
            latch.countDown();
          } else {
            latch.countDown();
            // success
          }
        });
      }
    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (ro.isError())
      fail(ro.getThrowable().getMessage());
  }

  private void doSaveList(List<JsonObject> list, Handler<AsyncResult<IWriteResult>> resultHandler) {
    WriteResult rr = new WriteResult();
    CounterObject counter = new CounterObject(list.size());
    ErrorObject<IWriteResult> ro = new ErrorObject<IWriteResult>();
    for (JsonObject entity : list) {
      doSave(entity, rr, result -> {
        if (result.failed()) {
          ro.setThrowable(result.cause());
        } else {
          // logger.info("saving " + counter.getCount());
          if (counter.reduce())
            resultHandler.handle(Future.succeededFuture(rr));
        }
      });
      if (ro.handleError(resultHandler))
        return;
    }
  }

  private void doSave(JsonObject entity, IWriteResult writeResult, Handler<AsyncResult<Void>> resultHandler) {
    MongoClient mongoClient = getMongoClient();
    logger.info("now saving");
    mongoClient.save(COLUMN, entity, result -> {
      if (result.failed()) {
        logger.info("failed", result.cause());
        Future<Void> future = Future.failedFuture(result.cause());
        resultHandler.handle(future);
        return;
      } else {
        logger.info("saved");
        WriteAction action = WriteAction.UNKNOWN;
        String id = result.result();
        if (id == null) {
          action = WriteAction.UPDATE;
        } else
          action = WriteAction.INSERT;
        writeResult.addEntry(null, id, action);
        resultHandler.handle(Future.succeededFuture());
      }
    });

  }

  private List<JsonObject> createRecords() {
    List<JsonObject> list = new ArrayList<JsonObject>();
    for (int i = 0; i < LOOP; i++) {
      JsonObject subjson = new JsonObject().put("subname", "subname").put("subnumber", i).put("secondProperty", true);
      JsonObject json = new JsonObject().put("name", "testname").put("number", i).put("subobject", subjson);
      list.add(json);
    }
    return list;
  }

  private void checkResult(IWriteResult rr, Handler<AsyncResult<Void>> resultHandler) {
    try {

      if (LOOP != rr.size()) {
        // check wether records weren't written or "only" IWriteResult is incomplete
        getMongoClient().count(
            COLUMN,
            new JsonObject(),
            queryResult -> {
              logger.error("incorrect result found in WriteResult, checking saved records");
              if (queryResult.failed()) {
                logger.error("query failed!", queryResult.cause());
                resultHandler.handle(Future.failedFuture(queryResult.cause()));
              } else {
                if (LOOP != queryResult.result()) {
                  resultHandler.handle(Future.failedFuture(new AssertionError(String.format(
                      "UNSAVED ENTITIES, expected: %d - found in Mongo: %d", LOOP, queryResult.result()))));
                } else {
                  logger.info("records in database OK");
                  resultHandler.handle(Future.failedFuture(new AssertionError(String.format(
                      "Wrong WriteResult, expected: %d - logged in WriteResult: %d", LOOP, rr.size()))));
                }
              }
            });
      } else {
        getMongoClient().count(
            COLUMN,
            new JsonObject(),
            queryResult -> {
              logger.error("correct result found in WriteResult, checking saved records");
              if (queryResult.failed()) {
                logger.error("", queryResult.cause());
                resultHandler.handle(Future.failedFuture(queryResult.cause()));
              } else {
                if (LOOP != queryResult.result()) {
                  resultHandler.handle(Future.failedFuture(new AssertionError(String.format(
                      "UNSAVED ENTITIES, expected: %d - found in Mongo: %d", LOOP, queryResult.result()))));
                } else {
                  resultHandler.handle(Future.succeededFuture());
                }
              }
            });
      }
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

}
