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
import java.util.Properties;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.init.DataStoreSettings;
import de.braintags.io.vertx.pojomapper.init.IDataStoreInit;
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
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * An initializer for {@link MongoDataStore}
 * 
 * @author Michael Remme
 * 
 */
public class MongoDataStoreInit implements IDataStoreInit {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(MongoDataStoreInit.class);

  /**
   * The name of the property to define, wether a local instance of Mongo shall be started. Usable only for debugging
   * and testing purpose
   */
  public static final String START_MONGO_LOCAL_PROP = "startMongoLocal";

  /**
   * If START_MONGO_LOCAL_PROP is set to true, then this defines the local port to be used. Default is 27018
   */
  public static final String LOCAL_PORT_PROP = "localPort";

  /**
   * The property, which defines the connection string. Something like "mongodb://localhost:27017"
   */
  public static final String CONNECTION_STRING_PROPERTY = "connection_string";

  /**
   * The default connection to be used, if CONNECTION_STRING_PROPERTY is undefined
   */
  public static final String DEFAULT_CONNECTION = "mongodb://localhost:27017";

  /**
   * The name of the property in the config, which defines the database name
   */
  public static final String DBNAME_PROP = "db_name";

  /**
   * The property, which defines wether referenced entities inside a mapper shall be handled recursive or not
   */
  public static final String HANDLE_REFERENCED_RECURSIVE_PROP = "handleReferencedRecursive";

  /**
   * The name of the property, which defines wether the MongoClient to be used is shared or not
   */
  public static final String SHARED_PROP = "shared";

  private Vertx vertx;
  private static MongodExecutable exe;
  private boolean startMongoLocal = false;
  private boolean handleReferencedRecursive = true;
  private int localPort = 27018;
  private boolean shared = false;
  private JsonObject config;
  private DataStoreSettings settings;
  private MongoClient mongoClient;
  private MongoDataStore mongoDataStore;

  /**
   * Helper method which creates the default settings for an instance of {@link MongoDataStore}
   * 
   * @return default instance of {@link DataStoreSettings} to init a {@link MongoDataStore}
   */
  public static final DataStoreSettings createDefaultSettings() {
    DataStoreSettings settings = new DataStoreSettings(MongoDataStoreInit.class, "testdatabase");
    settings.getProperties().put(CONNECTION_STRING_PROPERTY, DEFAULT_CONNECTION);
    settings.getProperties().put(START_MONGO_LOCAL_PROP, "false");
    settings.getProperties().put(LOCAL_PORT_PROP, "27018");
    settings.getProperties().put(SHARED_PROP, "false");
    settings.getProperties().put(HANDLE_REFERENCED_RECURSIVE_PROP, "true");
    return settings;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.init.IDataStoreInit#initDataStore(de.braintags.io.vertx.pojomapper.init.
   * DataStoreSettings, io.vertx.core.Handler)
   */
  @Override
  public void initDataStore(Vertx vertx, DataStoreSettings settings, Handler<AsyncResult<IDataStore>> handler) {
    try {
      this.vertx = vertx;
      this.settings = settings;
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
  public MongodExecutable getMongodExecutable() {
    return exe;
  }

  private void initMongoClient(Handler<AsyncResult<Void>> handler) {
    try {
      LOGGER.info("init MongoClient with " + settings);
      JsonObject config = getConfig();
      mongoClient = shared ? MongoClient.createShared(vertx, config) : MongoClient.createNonShared(vertx, config);
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

  private JsonObject getConfig() {
    if (config == null) {
      config = new JsonObject();
      config.put("connection_string", getConnectionString());
      config.put(DBNAME_PROP, getDatabaseName());
      config.put(IDataStore.HANDLE_REFERENCED_RECURSIVE, handleReferencedRecursive);
    }
    return config;
  }

  private void checkShared() {
    shared = getBooleanProperty(SHARED_PROP, shared);
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
   * Get the name of the database to be used
   * 
   * @return
   */
  private String getDatabaseName() {
    return settings.getDatabaseName();
  }

  /**
   * Get a property with the given key as boolean
   * 
   * @param name
   *          the key of the property to be fetched
   * @return the value as integer
   */
  private int getIntegerProperty(String name, int defaultValue) {
    return Integer.parseInt(getProperty(name, String.valueOf(defaultValue)));
  }

  /**
   * Get a property with the given key as boolean
   * 
   * @param name
   *          the key of the property to be fetched
   * @return true or false
   */
  private boolean getBooleanProperty(String name, boolean defaultValue) {
    return Boolean.parseBoolean(getProperty(name, String.valueOf(defaultValue)));
  }

  /**
   * Get a property with the given key
   * 
   * @param name
   *          the key of the property to be fetched
   * @return a valid value or null
   */
  private String getProperty(String name, String defaultValue) {
    Properties props = this.settings.getProperties();
    String s = (String) props.get(name);
    if (s != null) {
      s = s.trim();
      if (s.length() > 0) {
        return s;
      }
    }
    return defaultValue;
  }

  public static void startMongoExe(boolean startMongoLocal, int localPort) {
    LOGGER.info("STARTING MONGO");
    if (startMongoLocal) {
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
