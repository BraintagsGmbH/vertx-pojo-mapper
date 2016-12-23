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

import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.zatarox.vertx.async.AsyncFactorySingleton;
import io.zatarox.vertx.async.utils.DefaultAsyncResult;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

@RunWith(VertxUnitRunner.class)
public class TMongoDirectMassInsert {
  private static Logger LOGGER = LoggerFactory.getLogger(TMongoDirectMassInsert.class);
  private static final int LOOP = 500;
  private static final List<Integer> LOOPLIST = IntStream.iterate(0, i -> i + 1).limit(LOOP).boxed()
      .collect(Collectors.toList());

  private static final String TABLENAME = "massInsert";
  private static final boolean startMongoLocal = true;
  private static List<String> results = new ArrayList<>();
  private static MongoClient client;
  private static Vertx vertx;
  public static final String CONNECTION_STRING_PROPERTY = "connection_string";
  public static final String DEFAULT_CONNECTION = "mongodb://localhost:27017";
  public static final String DBNAME_PROP = "db_name";
  private static MongodExecutable exe;
  private static int localPort = 27017;

  @Test
  public void massInsertWithCounter(TestContext context) {
    Async async = context.async();
    dropTable(context, TABLENAME);

    final long startTime = System.currentTimeMillis();
    CounterObject<Void> co = new CounterObject<>(LOOP, null);
    for (int i = 0; i < LOOP; i++) {
      JsonObject jsonCommand = new JsonObject().put("name", "testName " + i);
      client.insert(TABLENAME, jsonCommand, result -> {
        if (result.failed()) {
          LOGGER.error("", result.cause());
          co.setThrowable(result.cause());
          async.complete();
        } else {
          LOGGER.info("executed: " + result.result());
          if (co.reduce()) {
            LOGGER.info("finished");
            client.count(TABLENAME, new JsonObject(), cr -> {
              if (cr.failed()) {
                LOGGER.error("", cr.cause());
                co.setThrowable(cr.cause());
                async.complete();
              } else {
                try {
                  context.assertEquals(LOOP, cr.result().intValue(), "result not correct");
                  async.complete();
                } catch (Exception e) {
                  LOGGER.error("", e);
                  co.setThrowable(e);
                  async.complete();
                }
              }
            });
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
    results.add("massInsertWithCounter: " + execution + " | ");
    LOGGER.info(results.toString());
  }

  @Test
  public void massInsertWithAsync(TestContext context) {
    ErrorObject err = new ErrorObject<>(null);
    Async async = context.async();
    dropTable(context, TABLENAME);
    final long startTime = System.currentTimeMillis();
    Context c = vertx.getOrCreateContext();
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
    results.add("massInsertWithAsync: " + execution + " | ");
    LOGGER.info(results.toString());
  }

  @BeforeClass
  public static void beforeTest() {
    vertx = Vertx.vertx();
    startMongoExe();
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
  }

  /**
   * Starts an instance of a local mongo, if startMongoLocal is true
   * 
   * @param startMongoLocal
   *          defines wether to start a local instance
   * @param localPort
   *          the port where to start the instance
   */
  private static void startMongoExe() {
    if (exe == null && startMongoLocal) {
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
