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

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoService;
import io.vertx.test.core.VertxTestBase;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;

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

  @BeforeClass
  public static void startMongo() {
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

  @AfterClass
  public static void stopMongo() {
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

    DeploymentOptions options = new DeploymentOptions().setConfig(config);
    CountDownLatch latch = new CountDownLatch(1);
    vertx.deployVerticle("service:io.vertx.mongo-service", options, onSuccess(id -> {
      mongoClient = MongoService.createEventBusProxy(vertx, "vertx.mongo");
      dropCollections(latch);
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
   * Method drops all collections which are starting with the prefix {@link #TABLE_PREFIX}
   * 
   * @param latch
   *          the latch to be used
   */
  protected void dropCollections(CountDownLatch latch) {
    log.info("DROPPING COLLECTIONS");
    // Drop all the collections in the db
    mongoClient.getCollections(onSuccess(toDrop -> {
      AtomicInteger collCount = new AtomicInteger();
      int count = toDrop.size();
      if (!toDrop.isEmpty()) {
        for (String collection : toDrop) {
          if (collection.startsWith("system.")) {
            latch.countDown();
          } else {
            mongoClient.dropCollection(collection, onSuccess(v -> {
              if (collCount.incrementAndGet() == count) {
                latch.countDown();
              }
            }));
          }
        }
      } else {
        latch.countDown();
      }
    }));
  }

}
