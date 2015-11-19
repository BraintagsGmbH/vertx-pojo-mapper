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

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.exception.InitException;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.testdatastore.IDatastoreContainer;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest;
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
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MongoDataStoreContainer implements IDatastoreContainer {
  private static final io.vertx.core.logging.Logger logger = io.vertx.core.logging.LoggerFactory
      .getLogger(MongoDataStoreContainer.class);

  private static final int LOCAL_PORT = 27018;
  private static final String START_MONGO_LOCAL_PROP = "startMongoLocal";
  public static final String CONNECTION_STRING_PROPERTY = "connection_string";
  public static final String DEFAULT_CONNECTION = "mongodb://localhost:27017";
  private static boolean handleReferencedRecursive = true;

  private static boolean startMongoLocal = true;

  private static MongodExecutable exe;
  private static MongoClient mongoClient;
  private MongoDataStore mongoDataStore;

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.datastoretest.IDatastoreContainer#startup(io.vertx.core.Vertx,
   * io.vertx.core.Handler)
   */
  @Override
  public void startup(Vertx vertx, Handler<AsyncResult<Void>> handler) {
    checkMongoLocal();
    startMongoExe();
    try {
      if (mongoDataStore == null) {
        logger.info("starting mongo datastore");
        initMongoClient(vertx, initResult -> {
          if (initResult.failed()) {
            logger.error("could not start mongo client", initResult.cause());
            handler.handle(Future.failedFuture(new InitException(initResult.cause())));
            return;
          }
          mongoDataStore = new MongoDataStore(vertx, mongoClient, getConfig());
          handler.handle(Future.succeededFuture());
          return;
        });
      } else {
        handler.handle(Future.succeededFuture());
      }
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.datastoretest.IDatastoreContainer#getDataStore()
   */
  @Override
  public IDataStore getDataStore() {
    return mongoDataStore;
  }

  private void initMongoClient(Vertx vertx, Handler<AsyncResult<Void>> handler) {
    if (mongoClient != null) {
      logger.info("MongoClient already initialized ");
      handler.handle(Future.succeededFuture());
      return;
    }

    try {
      JsonObject config = getConfig();
      logger.info("init MongoClient with " + config);
      mongoClient = MongoClient.createNonShared(vertx, config);
      if (mongoClient == null) {
        handler.handle(Future.failedFuture(new InitException("No MongoClient created")));
        return;
      }
    } catch (Exception e) {
      handler.handle(Future.failedFuture(new InitException(e)));
      return;
    }

    mongoClient.getCollections(resultHandler -> {
      if (resultHandler.failed()) {
        logger.error("", resultHandler.cause());
        handler.handle(Future.failedFuture(resultHandler.cause()));
      } else {
        logger.info(String.format("found %d collections", resultHandler.result().size()));
        handler.handle(Future.succeededFuture());
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.datastoretest.IDatastoreContainer#shutdown(io.vertx.core.Handler)
   */
  @Override
  public void shutdown(Handler<AsyncResult<Void>> handler) {
    logger.info("shutdown performed");
    mongoClient.close();
    mongoClient = null;
    mongoDataStore = null;
    if (exe != null) {
      exe.stop();
    }

    handler.handle(Future.succeededFuture());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.datastoretest.IDatastoreContainer#dropTable(java.lang.String,
   * io.vertx.core.Handler)
   */
  @Override
  public void dropTable(String collection, Handler<AsyncResult<Void>> handler) {
    logger.info("DROPPING: " + collection);
    mongoClient.dropCollection(collection, dropResult -> {
      if (dropResult.failed()) {
        logger.error("", dropResult.cause());
        handler.handle(dropResult);
        return;
      }
      handler.handle(dropResult);
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.IDatastoreContainer#clearTable(java.lang.String,
   * io.vertx.core.Handler)
   */
  @Override
  public void clearTable(String tablename, Handler<AsyncResult<Void>> handler) {
    dropTable(tablename, handler);
  }

  /**
   * Creates a config file for a mongo db
   * 
   * @return the prepared config file with the connection string and the database name to be used
   */
  protected static JsonObject getConfig() {
    JsonObject config = new JsonObject();
    config.put("connection_string", getConnectionString());
    config.put("db_name", getDatabaseName());
    config.put(IDataStore.HANDLE_REFERENCED_RECURSIVE, handleReferencedRecursive);
    return config;
  }

  /**
   * returns true if a local instance of Mongo shall be started
   * 
   * @return
   */
  protected static void checkMongoLocal() {
    startMongoLocal = Boolean.parseBoolean(System.getProperty(START_MONGO_LOCAL_PROP, "false"));
  }

  /**
   * Get the connection String for the mongo db
   * 
   * @return
   */
  protected static String getConnectionString() {
    if (startMongoLocal) {
      return "mongodb://localhost:" + LOCAL_PORT;
    } else {
      return getProperty(CONNECTION_STRING_PROPERTY, DEFAULT_CONNECTION);
    }
  }

  /**
   * Get the name of the database to be used
   * 
   * @return
   */
  protected static String getDatabaseName() {
    return getProperty("db_name", "PojongoTestDatabase");
  }

  /**
   * Get a property with the given key
   * 
   * @param name
   *          the key of the property to be fetched
   * @return a valid value or null
   */
  protected static String getProperty(String name, String defaultValue) {
    String s = System.getProperty(name);
    if (s != null) {
      s = s.trim();
      if (s.length() > 0) {
        return s;
      }
    }
    return defaultValue;
  }

  public static void startMongoExe() {
    logger.info("STARTING MONGO");
    if (startMongoLocal) {
      try {
        IMongodConfig config = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
            .net(new Net(LOCAL_PORT, Network.localhostIsIPv6())).build();
        exe = MongodStarter.getDefaultInstance().prepare(config);
        exe.start();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public String getExpectedTypehandlerName(Class<? extends AbstractTypeHandlerTest> testClass, String defaultName) {
    return defaultName;
  }

}
