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
package de.braintags.io.vertx.pojomapper.mongo.vertxunit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.braintags.io.vertx.util.ErrorObject;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

@RunWith(VertxUnitRunner.class)
public class TMongoDirectMassInsert {
  private static Logger LOGGER = LoggerFactory.getLogger(TMongoDirectMassInsert.class);
  private static final int LOOP = 200;

  private static final String TABLENAME = "massInsert";
  private static final boolean startMongoLocal = false;
  private static List<String> results = new ArrayList<>();
  private static MongoClient client;
  private static Vertx vertx;
  public static final String CONNECTION_STRING_PROPERTY = "connection_string";
  public static final String DEFAULT_CONNECTION = "mongodb://localhost:27017";
  public static final String DBNAME_PROP = "db_name";
  private static MongodExecutable exe;
  private static int localPort = 27017;

  @SuppressWarnings("rawtypes")
  @Test
  public void massInsertWithComposite(TestContext context) {
    ErrorObject err = new ErrorObject<>(null);
    Async async = context.async();
    dropTable(context);
    final long startTime = System.currentTimeMillis();
    List<Future> futureList = createFutureList(TABLENAME);
    CompositeFuture cf = CompositeFuture.all(futureList);
    cf.setHandler(result -> {
      if (result.failed()) {
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
            } catch (Throwable e) {
              LOGGER.error("", e);
              err.setThrowable(e);
              async.complete();
            }
          }
        });
      }
    });

    async.await();
    if (err.isError()) {
      context.fail(err.getThrowable());
    }
    long execution = System.currentTimeMillis() - startTime;
    results.add("massInsertWithComposite: " + execution + " | ");
    LOGGER.info(results.toString());
  }

  @SuppressWarnings("rawtypes")
  private List<Future> createFutureList(String tableName) {
    List<Future> futureList = new ArrayList<>();
    for (int i = 0; i < LOOP; i++) {
      futureList.add(handleEntry(tableName, i));
    }
    return futureList;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Future handleEntry(String tableName, int number) {
    Future f = Future.future();
    JsonObject jsonCommand = new JsonObject().put("name", "testName " + number);
    client.insert(tableName, jsonCommand, f.completer());
    return f;
  }

  @Test
  public void massInsertWithCounter(TestContext context) {
    Async async = context.async();
    dropTable(context);

    final long startTime = System.currentTimeMillis();
    @SuppressWarnings("rawtypes")
    List<Future> futures = new ArrayList<>();
    for (int i = 0; i < LOOP; i++) {
      Future<String> future = Future.future();
      futures.add(future);
      JsonObject jsonCommand = new JsonObject().put("name", "testName " + i);
      client.insert(TABLENAME, jsonCommand, future.completer());
    }

    CompositeFuture.all(futures).setHandler(result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
        context.fail(result.cause());
      } else {
        client.count(TABLENAME, new JsonObject(), cr -> {
          if (cr.failed()) {
            LOGGER.error("", cr.cause());
            context.fail(cr.cause());
          } else {
            int count = cr.result().intValue();
            LOGGER.debug("got " + count + " results");
            context.assertEquals(LOOP, count, "result not correct");
            long execution = System.currentTimeMillis() - startTime;
            results.add("massInsertWithCounter: " + execution + " | ");
            LOGGER.info(results.toString());
            async.complete();
          }
        });
      }
    });
  }

  @BeforeClass
  public static void beforeTest(TestContext testContext) {
    vertx = Vertx.vertx();
    vertx.exceptionHandler(testContext.exceptionHandler());
    startMongoExe(startMongoLocal);
    client = MongoClient.createShared(vertx, createConfig());
  }

  private static JsonObject createConfig() {
    JsonObject config = new JsonObject();
    config.put("connection_string", System.getProperty(CONNECTION_STRING_PROPERTY, DEFAULT_CONNECTION));
    config.put(DBNAME_PROP, TMongoDirectMassInsert.class.getSimpleName());
    return config;
  }

  private static void dropTable(TestContext context) {
    Async async = context.async();
    ErrorObject<Void> err = new ErrorObject<>(null);
    client.dropCollection(TABLENAME, dropResult -> {
      if (dropResult.failed()) {
        err.setThrowable(dropResult.cause());
        async.complete();
      } else {
        async.complete();
      }
    });
    async.await();
  }

  /**
   * Starts an instance of a local mongo, if startMongoLocal is true
   * 
   * @param startMongoLocal
   *          defines wether to start a local instance
   * @param localPort
   *          the port where to start the instance
   */
  private static void startMongoExe(boolean startLocal) {
    if (exe == null && startLocal) {
      LOGGER.info("STARTING LOCAL MONGO");
      try {
        IMongodConfig config = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
            .net(new Net(localPort, Network.localhostIsIPv6())).build();
        exe = MongodStarter.getDefaultInstance().prepare(config);
        exe.start();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

}
