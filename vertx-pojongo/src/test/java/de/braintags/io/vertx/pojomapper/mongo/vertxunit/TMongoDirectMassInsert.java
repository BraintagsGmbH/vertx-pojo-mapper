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
package de.braintags.io.vertx.pojomapper.mongo.vertxunit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.testdatastore.TestHelper;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.zatarox.vertx.async.AsyncFactorySingleton;
import io.zatarox.vertx.async.utils.DefaultAsyncResult;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class TMongoDirectMassInsert extends DatastoreBaseTest {
  private static Logger LOGGER = LoggerFactory.getLogger(TMongoDirectMassInsert.class);
  private static final int LOOP = 500;
  private static final List<Integer> LOOPLIST = IntStream.iterate(0, i -> i + 1).limit(LOOP).boxed()
      .collect(Collectors.toList());

  private static final String TABLENAME = "massInsert";
  private static List<String> results = new ArrayList<>();

  @Test
  public void massInsertWithCounter(TestContext context) {
    ErrorObject err = new ErrorObject<>(null);
    Async async = context.async();
    dropTable(context, TABLENAME);
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    MongoClient client = (MongoClient) ds.getClient();

    final long startTime = System.currentTimeMillis();
    CounterObject<Void> co = new CounterObject<>(LOOP, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
        err.setThrowable(result.cause());
        async.complete();
      } else {
        LOGGER.info("finished");
        client.count(TABLENAME, new JsonObject(), cr -> {
          if (cr.failed()) {
            LOGGER.error("", cr.cause());
            err.setThrowable(cr.cause());
            async.complete();
          } else {
            try {
              context.assertEquals(LOOP, cr.result().intValue(), "result not correct");
              async.complete();
            } catch (Exception e) {
              LOGGER.error("", e);
              err.setThrowable(e);
              async.complete();
            }
          }
        });
      }
    });
    for (int i = 0; i < LOOP; i++) {
      JsonObject jsonCommand = new JsonObject().put("name", "testName " + i);
      client.insert(TABLENAME, jsonCommand, result -> {
        if (result.failed()) {
          co.setThrowable(result.cause());
          err.setThrowable(result.cause());
          LOGGER.error("", result.cause());
          async.complete();
        } else {
          LOGGER.info("executed: " + result.result());
          if (co.reduce()) {
            co.setResult(null);
            async.complete();
          }
        }
      });
      if (co.isError()) {
        break;
      }
    }

    async.await();
    if (co.isError()) {
      context.fail(co.getThrowable());
    }
    long execution = System.currentTimeMillis() - startTime;
    results.add("massInsertWithCounter" + execution + " | ");
    LOGGER.info(results.toString());
  }

  @Test
  public void massInsertWithAsync(TestContext context) {
    ErrorObject err = new ErrorObject<>(null);
    Async async = context.async();
    dropTable(context, TABLENAME);
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    MongoClient client = (MongoClient) ds.getClient();

    final long startTime = System.currentTimeMillis();
    Context c = TestHelper.vertx.getOrCreateContext();
    if (c == null) {
      throw new NullPointerException();
    }
    Handler<AsyncResult<Void>> h = new Handler<AsyncResult<Void>>() {
      @Override
      public void handle(AsyncResult<Void> result) {
        LOGGER.info("finished");
        client.count(TABLENAME, new JsonObject(), cr -> {
          if (cr.failed()) {
            LOGGER.error("", cr.cause());
            err.setThrowable(cr.cause());
            async.complete();
          } else {
            try {
              context.assertEquals(LOOP, cr.result().intValue(), "result not correct");
              async.complete();
            } catch (Throwable e) {
              LOGGER.error("", e);
              err.setThrowable(e);
              async.complete();
            }
          }
        });
      }
    };
    Future<Void> f = Future.future();
    f.setHandler(h);

    AsyncFactorySingleton.getInstance().createCollections(c).each(LOOPLIST, (item, handler) -> {
      JsonObject jsonCommand = new JsonObject().put("name", "testName " + item);
      client.insert(TABLENAME, jsonCommand, result -> {
        LOGGER.info("executed: " + item);
        handler.handle(DefaultAsyncResult.succeed());
      });
    }, res -> {
      f.complete();
    });

    async.await();
    if (err.isError()) {
      context.fail(err.getThrowable());
    }

    long execution = System.currentTimeMillis() - startTime;
    results.add("massInsertWithAsync" + execution + " | ");
    LOGGER.info(results.toString());
  }

}
