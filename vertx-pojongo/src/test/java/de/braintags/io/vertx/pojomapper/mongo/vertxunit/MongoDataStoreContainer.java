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

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.exception.InitException;
import de.braintags.io.vertx.pojomapper.init.DataStoreSettings;
import de.braintags.io.vertx.pojomapper.init.IDataStoreInit;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.mongo.init.MongoDataStoreInit;
import de.braintags.io.vertx.pojomapper.testdatastore.IDatastoreContainer;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest;
import de.flapdoodle.embed.mongo.MongodExecutable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

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

  private static MongodExecutable exe;
  private MongoDataStore mongoDataStore;

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.datastoretest.IDatastoreContainer#startup(io.vertx.core.Vertx,
   * io.vertx.core.Handler)
   */
  @Override
  public void startup(Vertx vertx, Handler<AsyncResult<Void>> handler) {
    try {
      if (mongoDataStore == null) {
        DataStoreSettings settings = createSettings();
        IDataStoreInit dsInit = settings.getDatastoreInit().newInstance();
        dsInit.initDataStore(vertx, settings, initResult -> {
          if (initResult.failed()) {
            logger.error("could not start mongo client", initResult.cause());
            handler.handle(Future.failedFuture(new InitException(initResult.cause())));
          } else {
            mongoDataStore = (MongoDataStore) initResult.result();
            handler.handle(Future.succeededFuture());
          }
        });
      } else {
        handler.handle(Future.succeededFuture());
      }
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  private DataStoreSettings createSettings() {
    DataStoreSettings settings = new DataStoreSettings();
    settings.setDatastoreInit(MongoDataStoreInit.class);
    settings.getProperties().put(MongoDataStoreInit.CONNECTION_STRING_PROPERTY,
        getProperty(CONNECTION_STRING_PROPERTY, DEFAULT_CONNECTION));
    settings.getProperties().put(MongoDataStoreInit.START_MONGO_LOCAL_PROP,
        Boolean.parseBoolean(System.getProperty(START_MONGO_LOCAL_PROP, "false")));
    settings.getProperties().put(MongoDataStoreInit.LOCAL_PORT_PROP, String.valueOf(LOCAL_PORT));
    settings.getProperties().put(MongoDataStoreInit.DBNAME_PROP, getProperty("db_name", "PojongoTestDatabase"));
    settings.getProperties().put(MongoDataStoreInit.SHARED_PROP, "false");
    settings.getProperties().put(MongoDataStoreInit.HANDLE_REFERENCED_RECURSIVE_PROP, handleReferencedRecursive);
    return settings;
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

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.datastoretest.IDatastoreContainer#shutdown(io.vertx.core.Handler)
   */
  @Override
  public void shutdown(Handler<AsyncResult<Void>> handler) {
    logger.info("shutdown performed");
    mongoDataStore.shutdown(result -> {
      if (result.failed()) {
        logger.error("", result.cause());
      }
      mongoDataStore = null;
      if (exe != null) {
        exe.stop();
      }
      handler.handle(Future.succeededFuture());

    });
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
    mongoDataStore.getMongoClient().dropCollection(collection, dropResult -> {
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
   * Get a property with the given key
   * 
   * @param name
   *          the key of the property to be fetched
   * @return a valid value or null
   */
  private static String getProperty(String name, String defaultValue) {
    String s = System.getProperty(name);
    if (s != null) {
      s = s.trim();
      if (s.length() > 0) {
        return s;
      }
    }
    return defaultValue;
  }

  @Override
  public String getExpectedTypehandlerName(Class<? extends AbstractTypeHandlerTest> testClass, String defaultName) {
    return defaultName;
  }

}
