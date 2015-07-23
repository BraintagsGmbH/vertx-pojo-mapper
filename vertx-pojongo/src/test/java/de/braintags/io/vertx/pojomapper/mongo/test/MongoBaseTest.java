/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mongo.test;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoService;
import io.vertx.test.core.VertxTestBase;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * @author mremme
 */

public abstract class MongoBaseTest extends VertxTestBase {
  private static final Logger log = LoggerFactory.getLogger(MongoBaseTest.class);

  private static MongodExecutable exe;
  private static MongoClient mongoClient;
  private MongoDataStore mongoDataStore;

  /**
   * Get the connection String for the mongo db
   * 
   * @return
   */
  protected static String getConnectionString() {
    return getProperty("connection_string");
  }

  /**
   * Get the name of the database to be used
   * 
   * @return
   */
  protected static String getDatabaseName() {
    return getProperty("db_name");
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.test.core.VertxTestBase#setUp()
   */
  @Override
  public final void setUp() throws Exception {
    log.info("-->> setup");
    super.setUp();
    getMongoClient();
    dropCollections();
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.test.core.VertxTestBase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    log.info("tearDown");
    super.tearDown();
    mongoClient.close();
    mongoClient = null;
  }

  /**
   * Get a property with the given key
   * 
   * @param name
   *          the key of the property to be fetched
   * @return a valid value or null
   */
  protected static String getProperty(String name) {
    String s = System.getProperty(name);
    if (s != null) {
      s = s.trim();
      if (s.length() > 0) {
        return s;
      }
    }
    return null;
  }

  public static void startMongo() {
    log.info("STARTING MONGO");
    if (getConnectionString() == null) {
      try {
        IMongodConfig config = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
            .net(new Net(27018, Network.localhostIsIPv6())).build();
        exe = MongodStarter.getDefaultInstance().prepare(config);
        exe.start();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void stopMongo() {
    log.info("STOPPING MONGO");
    if (mongoClient != null)
      mongoClient.close();
    if (exe != null) {
      exe.stop();
    }
  }

  /**
   * If instance of MongoService is null, initialization is performed
   * 
   * @return the current instance of {@link MongoClient}
   * @throws Exception
   *           any Exception by submethods
   */
  public MongoClient getMongoClient() {
    if (mongoClient == null) {
      initMongoClient();
    }
    return mongoClient;
  }

  private void initMongoClient() {
    JsonObject config = getConfig();
    log.info("init MongoClient with " + config);
    mongoClient = MongoClient.createShared(vertx, config);
    CountDownLatch latch = new CountDownLatch(1);
    mongoClient.getCollections(resultHandler -> {
      if (resultHandler.failed()) {
        log.error("", resultHandler.cause());
      } else {
        log.info(String.format("found %d collections", resultHandler.result().size()));
      }
      latch.countDown();
    });
    try {
      awaitLatch(latch);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unused")
  private void initMongoService() {
    JsonObject config = getConfig();
    log.info("init MongoService with " + config);
    DeploymentOptions options = new DeploymentOptions().setConfig(config);
    CountDownLatch latch = new CountDownLatch(1);
    vertx.deployVerticle("service:io.vertx.mongo-service", options, onSuccess(id -> {
      mongoClient = MongoService.createEventBusProxy(vertx, "vertx.mongo");
      latch.countDown();
      ;
    }));
    try {
      awaitLatch(latch);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

  }

  public MongoDataStore getDataStore() {
    if (mongoDataStore == null) {
      mongoDataStore = new MongoDataStore(getMongoClient());
    }
    return mongoDataStore;
  }

  /**
   * Creates a config file for a mongo db
   * 
   * @return the prepared config file with the connection string and the database name to be used
   */
  protected static JsonObject getConfig() {
    JsonObject config = new JsonObject();
    String connectionString = getConnectionString();
    if (connectionString != null) {
      config.put("connection_string", connectionString);
    } else {
      config.put("connection_string", "mongodb://localhost:27018");
    }
    String databaseName = getDatabaseName();
    if (databaseName != null) {
      config.put("db_name", databaseName);
    }
    return config;
  }

  /**
   * Method drops all non system collections
   * 
   * @param latch
   *          the latch to be used
   */
  protected void dropCollections() {
    log.info("DROPPING COLLECTIONS");
    // Drop all the collections in the db
    CountDownLatch externalLatch = new CountDownLatch(1);
    mongoClient.getCollections(colls -> {
      log.info("handling collections result");
      if (colls.failed()) {
        log.error(colls.cause());
      } else {
        List<String> collections = colls.result();
        CountDownLatch internalLatch = new CountDownLatch(collections.size());
        for (String collection : collections) {
          if (collection.startsWith("system.")) {
            log.info("NOT Dropping: " + collection);
            internalLatch.countDown();
          } else {
            mongoClient.dropCollection(collection, dropResult -> {
              log.info("DROPPING: " + collection);
              if (dropResult.failed()) {
                log.error("", dropResult.cause());
              }
              internalLatch.countDown();
            });
          }
        }
        try {
          awaitLatch(internalLatch);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
      externalLatch.countDown();
    });

    try {
      awaitLatch(externalLatch);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void checkWriteResult(AsyncResult<IWriteResult> result) {
    assertTrue(resultFine(result));
    assertNotNull(result.result());
    assertNotNull(result.result().getStoreObject());
    assertNotNull(result.result().getId());
  }

  public boolean resultFine(AsyncResult<?> result) {
    if (result.failed()) {
      log.error("", result.cause());
      return false;
    }
    return true;
  }

}
