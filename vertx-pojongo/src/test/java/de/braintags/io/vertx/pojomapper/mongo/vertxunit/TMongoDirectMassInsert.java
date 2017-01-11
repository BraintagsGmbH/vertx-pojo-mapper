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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
  private static final List<Integer> LOOPLIST = IntStream.iterate(0, i -> i + 1).limit(LOOP).boxed()
      .collect(Collectors.toList());

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
    dropTable(context, TABLENAME);
    final long startTime = System.currentTimeMillis();
    List<Future> futureList = createFutureList();
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
  private List<Future> createFutureList() {
    List<Future> futureList = new ArrayList<>();
    for (int i = 0; i < LOOP; i++) {
      futureList.add(handleEntry(i));
    }
    return futureList;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Future handleEntry(int number) {
    Future f = Future.future();
    JsonObject jsonCommand = new JsonObject().put("name", "testName " + number);
    client.insert(TABLENAME, jsonCommand, f.completer());
    return f;
  }

  @BeforeClass
  public static void beforeTest() {
    vertx = Vertx.vertx();
    startMongoExe(startMongoLocal);
    client = MongoClient.createShared(vertx, createConfig());
  }

  private static JsonObject createConfig() {
    JsonObject config = new JsonObject();
    config.put("connection_string", System.getProperty(CONNECTION_STRING_PROPERTY, DEFAULT_CONNECTION));
    config.put(DBNAME_PROP, TMongoDirectMassInsert.class.getSimpleName());
    return config;
  }

  public static void dropTable(TestContext context, String collection) {
    Async async = context.async();
    ErrorObject<Void> err = new ErrorObject<>(null);
    client.dropCollection(collection, dropResult -> {
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
