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
package de.braintags.io.vertx.pojomapper.mongo.init;

import java.io.IOException;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.init.AbstractDataStoreInit;
import de.braintags.io.vertx.pojomapper.init.DataStoreSettings;
import de.braintags.io.vertx.pojomapper.init.IDataStoreInit;
import de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.impl.keygen.DefaultKeyGenerator;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.util.exception.InitException;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * An initializer for {@link MongoDataStore}
 * 
 * @author Michael Remme
 * 
 */
public class MongoDataStoreInit extends AbstractDataStoreInit implements IDataStoreInit {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(MongoDataStoreInit.class);

  /**
   * The property, which defines the connection string. Something like "mongodb://localhost:27017"
   */
  public static final String CONNECTION_STRING_PROPERTY = "connection_string";

  /**
   * The name of the property to define, wether a local instance of Mongo shall be started. Usable only for debugging
   * and testing purpose
   */
  public static final String START_MONGO_LOCAL_PROP = "startMongoLocal";

  /**
   * If START_MONGO_LOCAL_PROP is set to true, then this defines the local port to be used. Default is 27017
   */
  public static final String LOCAL_PORT_PROP = "localPort";

  /**
   * The default connection to be used, if CONNECTION_STRING_PROPERTY is undefined
   */
  public static final String DEFAULT_CONNECTION = "mongodb://localhost:27017";
  private static final String DEFAULT_KEY_GENERATOR = DefaultKeyGenerator.NAME;

  private static MongodExecutable exe;
  boolean startMongoLocal = false;
  private MongoClient mongoClient;
  private MongoDataStore mongoDataStore;
  protected int localPort = 27017;

  /**
   * Helper method which creates the default settings for an instance of {@link MongoDataStore}
   * 
   * @return default instance of {@link DataStoreSettings} to init a {@link MongoDataStore}
   */
  public static final DataStoreSettings createDefaultSettings() {
    DataStoreSettings settings = new DataStoreSettings(MongoDataStoreInit.class, "testdatabase");
    settings.getProperties().put(CONNECTION_STRING_PROPERTY, DEFAULT_CONNECTION);
    settings.getProperties().put(START_MONGO_LOCAL_PROP, "false");
    settings.getProperties().put(LOCAL_PORT_PROP, "27017");
    settings.getProperties().put(SHARED_PROP, "false");
    settings.getProperties().put(HANDLE_REFERENCED_RECURSIVE_PROP, "true");
    settings.getProperties().put(IKeyGenerator.DEFAULT_KEY_GENERATOR, DEFAULT_KEY_GENERATOR);
    return settings;
  }

  @Override
  protected void internalInit(Handler<AsyncResult<IDataStore>> handler) {
    try {
      checkMongoLocal();
      checkShared();
      startMongoExe(startMongoLocal, localPort);
      initMongoClient(initResult -> {
        if (initResult.failed()) {
          LOGGER.error("could not start mongo client", initResult.cause());
          handler.handle(Future.failedFuture(new InitException(initResult.cause())));
        } else {
          mongoDataStore = new MongoDataStore(vertx, mongoClient, getConfig());
          handler.handle(Future.succeededFuture(mongoDataStore));
        }
      });
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  /**
   * Get the instance of MongoClient, which was created during init.
   * 
   * @return
   */
  public MongoClient getMongoClient() {
    return mongoClient;
  }

  /**
   * If for debugging purpose an internal MongodExecutable was started, it is returned here
   * 
   * @return the instance of MongodExecutable or null, if not used
   */
  public static MongodExecutable getMongodExecutable() {
    return exe;
  }

  private void initMongoClient(Handler<AsyncResult<Void>> handler) {
    try {
      LOGGER.info("init MongoClient with " + settings);
      JsonObject jconfig = getConfig();
      mongoClient = shared ? MongoClient.createShared(vertx, jconfig) : MongoClient.createNonShared(vertx, jconfig);
      if (mongoClient == null) {
        handler.handle(Future.failedFuture(new InitException("No MongoClient created")));
      } else {
        mongoClient.getCollections(resultHandler -> {
          if (resultHandler.failed()) {
            LOGGER.error("", resultHandler.cause());
            handler.handle(Future.failedFuture(resultHandler.cause()));
          } else {
            LOGGER.info(String.format("found %d collections", resultHandler.result().size()));
            handler.handle(Future.succeededFuture());
          }
        });
      }
    } catch (Exception e) {
      handler.handle(Future.failedFuture(new InitException(e)));
    }
  }

  @Override
  protected JsonObject createConfig() {
    JsonObject config = new JsonObject();
    config.put("connection_string", getConnectionString());
    config.put(DBNAME_PROP, getDatabaseName());
    config.put(IDataStore.HANDLE_REFERENCED_RECURSIVE, handleReferencedRecursive);
    String prop = getProperty(IKeyGenerator.DEFAULT_KEY_GENERATOR, null);
    config.put(IKeyGenerator.DEFAULT_KEY_GENERATOR, prop);

    return config;
  }

  /**
   * Get the connection String for the mongo db
   * 
   * @return
   */
  private String getConnectionString() {
    if (startMongoLocal) {
      return "mongodb://localhost:" + localPort;
    } else {
      return getProperty(CONNECTION_STRING_PROPERTY, DEFAULT_CONNECTION);
    }
  }

  /**
   * returns true if a local instance of Mongo shall be started
   * 
   * @return
   */
  private void checkMongoLocal() {
    startMongoLocal = getBooleanProperty(START_MONGO_LOCAL_PROP, false);
    localPort = getIntegerProperty(LOCAL_PORT_PROP, localPort);
  }

  /**
   * Starts an instance of a local mongo, if startMongoLocal is true
   * 
   * @param startMongoLocal
   *          defines wether to start a local instance
   * @param localPort
   *          the port where to start the instance
   */
  private static void startMongoExe(boolean startMongoLocal, int localPort) {
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
