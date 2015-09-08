/*
 * Copyright 2015 Braintags GmbH
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

package de.braintags.io.vertx.pojomapper.mongo.test;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.test.ITestInit;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class MongoTestInit implements ITestInit {
  private static final Logger logger = LoggerFactory.getLogger(MongoTestInit.class);
  private static MongodExecutable exe;
  private static MongoClient mongoClient;
  private MongoDataStore mongoDataStore;

  /**
   * 
   */
  public MongoTestInit() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.test.ITestInit#initDataStore()
   */
  @Override
  public void initDataStore() {
    getMongoClient();
  }

  @Override
  public IDataStore getDataStore() {
    if (mongoDataStore == null) {
      mongoDataStore = new MongoDataStore(getMongoClient());
    }
    return mongoDataStore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.test.ITestInit#closeDataStore()
   */
  @Override
  public void closeDataStore() {
    mongoClient.close();
    mongoClient = null;
  }

  public static void startMongo() {
    logger.info("STARTING MONGO");
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
    logger.info("STOPPING MONGO");
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
    logger.info("init MongoClient with " + config);
    mongoClient = MongoClient.createNonShared(vertx, config);
    CountDownLatch latch = new CountDownLatch(1);
    mongoClient.getCollections(resultHandler -> {
      if (resultHandler.failed()) {
        logger.error("", resultHandler.cause());
      } else {
        logger.info(String.format("found %d collections", resultHandler.result().size()));
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
    logger.info("init MongoService with " + config);
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
}
