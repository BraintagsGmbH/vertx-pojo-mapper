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

import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
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
  private static final Logger logger = LoggerFactory.getLogger(MongoBaseTest.class);

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
    logger.info("-->> setup");
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
    logger.info("tearDown");
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
    logger.info("DROPPING COLLECTIONS");
    // Drop all the collections in the db
    CountDownLatch externalLatch = new CountDownLatch(1);
    mongoClient.getCollections(colls -> {
      logger.info("handling collections result");
      if (colls.failed()) {
        logger.error(colls.cause());
      } else {
        List<String> collections = colls.result();
        CountDownLatch internalLatch = new CountDownLatch(collections.size());
        for (String collection : collections) {
          if (collection.startsWith("system.")) {
            logger.info("NOT Dropping: " + collection);
            internalLatch.countDown();
          } else {
            mongoClient.dropCollection(collection, dropResult -> {
              logger.info("DROPPING: " + collection);
              if (dropResult.failed()) {
                logger.error("", dropResult.cause());
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

  /**
   * Executes a query and checks for the expected result
   * 
   * @param query
   *          the query to be executed
   * @param expectedResult
   *          the expected number of records
   * @return ResultContainer with certain informations
   */
  public ResultContainer find(IQuery<?> query, int expectedResult) {
    ResultContainer resultContainer = new ResultContainer();
    CountDownLatch latch = new CountDownLatch(1);
    query.execute(result -> {
      try {
        resultContainer.queryResult = result.result();
        checkQueryResult(result, expectedResult);

        assertEquals(expectedResult, resultContainer.queryResult.size());
        logger.info(resultContainer.queryResult.getOriginalQuery());

      } catch (AssertionError e) {
        resultContainer.assertionError = e;
      } catch (Throwable e) {
        resultContainer.assertionError = new AssertionError(e);
      } finally {
        latch.countDown();
      }
    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return resultContainer;
  }

  /**
   * Executes a query and checks for the expected result
   * 
   * @param query
   *          the query to be executed
   * @param expectedResult
   *          the expected number of records
   * @return ResultContainer with certain informations
   */
  public ResultContainer findCount(IQuery<?> query, int expectedResult) {
    ResultContainer resultContainer = new ResultContainer();
    CountDownLatch latch = new CountDownLatch(1);
    query.executeCount(result -> {
      try {
        resultContainer.queryResultCount = result.result();
        checkQueryResultCount(result, expectedResult);
        logger.info(resultContainer.queryResultCount.getOriginalQuery());
      } catch (AssertionError e) {
        resultContainer.assertionError = e;
      } catch (Throwable e) {
        resultContainer.assertionError = new AssertionError(e);
      } finally {
        latch.countDown();
      }
    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return resultContainer;
  }

  /**
   * Performs the delete action and processes the checkQuery to improve the correct result
   * 
   * @param delete
   *          the {@link IDelete} to be executed
   * @param checkQuery
   *          the query to improve the correct result
   * @param expectedResult
   *          the expected result of checkQuery
   * @return {@link ResultContainer} with deleteResult and queryResult
   */
  public ResultContainer delete(IDelete<?> delete, IQuery<?> checkQuery, int expectedResult) {
    ResultContainer resultContainer = new ResultContainer();
    CountDownLatch latch = new CountDownLatch(1);
    delete.delete(result -> {
      try {
        resultContainer.deleteResult = result.result();
        checkDeleteResult(result);

        ResultContainer queryResult = find(checkQuery, expectedResult);
        resultContainer.assertionError = queryResult.assertionError;
        resultContainer.queryResult = queryResult.queryResult;
      } catch (AssertionError e) {
        resultContainer.assertionError = e;
      } catch (Throwable e) {
        resultContainer.assertionError = new AssertionError(e);
      } finally {
        latch.countDown();
      }
    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return resultContainer;
  }

  public void checkDeleteResult(AsyncResult<? extends IDeleteResult> dResult) {
    assertTrue(resultFine(dResult));
    IDeleteResult dr = dResult.result();
    assertNotNull(dr);
    assertNotNull(dr.getOriginalCommand());
    logger.info(dr.getOriginalCommand());
  }

  public void checkQueryResultCount(AsyncResult<? extends IQueryCountResult> qResult, int expectedResult) {
    CountDownLatch latch = new CountDownLatch(1);
    assertTrue(resultFine(qResult));
    IQueryCountResult qr = qResult.result();
    assertNotNull(qr);
    assertEquals(expectedResult, qr.getCount());
  }

  public void checkQueryResult(AsyncResult<? extends IQueryResult<?>> qResult, int expectedResult) {
    CountDownLatch latch = new CountDownLatch(1);
    assertTrue(resultFine(qResult));
    IQueryResult<?> qr = qResult.result();
    assertNotNull(qr);
    if (expectedResult == 0) {
      try {
        assertFalse(qr.iterator().hasNext());
      } finally {
        latch.countDown();
      }
    } else {
      assertTrue(qr.iterator().hasNext());
      qr.iterator().next(
          result -> {
            try {
              if (result.failed()) {
                result.cause().printStackTrace();
                throw result.cause() instanceof RuntimeException ? (RuntimeException) result.cause()
                    : new RuntimeException(result.cause());
              } else {
                assertNotNull(result.result());
              }
            } finally {
              latch.countDown();
            }
          });
    }

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public ResultContainer saveRecords(List<?> records) {
    ResultContainer resultContainer = new ResultContainer();
    CountDownLatch latch = new CountDownLatch(1);
    IWrite<Object> write = (IWrite<Object>) getDataStore().createWrite(records.get(0).getClass());
    for (Object record : records) {
      write.add(record);
    }
    write.save(result -> {
      try {
        resultContainer.writeResult = result.result();
        checkWriteResult(result);
      } catch (AssertionError e) {
        resultContainer.assertionError = e;
      } catch (Throwable e) {
        resultContainer.assertionError = new AssertionError(e);
      } finally {
        latch.countDown();
      }
    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return resultContainer;
  }

  public ResultContainer saveRecord(Object sm) {
    ResultContainer resultContainer = new ResultContainer();
    CountDownLatch latch = new CountDownLatch(1);
    IWrite<Object> write = (IWrite<Object>) getDataStore().createWrite(sm.getClass());
    write.add(sm);
    write.save(result -> {
      try {
        resultContainer.writeResult = result.result();
        checkWriteResult(result);
      } catch (AssertionError e) {
        resultContainer.assertionError = e;
      } catch (Throwable e) {
        resultContainer.assertionError = new AssertionError(e);
      } finally {
        latch.countDown();
      }
    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return resultContainer;
  }

  public void checkWriteResult(AsyncResult<IWriteResult> result) {
    assertTrue(resultFine(result));
    assertNotNull(result.result());
    IWriteEntry entry = result.result().iterator().next();
    assertNotNull(entry);
    assertNotNull(entry.getStoreObject());
    assertNotNull(entry.getId());
  }

  public boolean resultFine(AsyncResult<?> result) {
    if (result.failed()) {
      logger.error("", result.cause());
      return false;
    }
    return true;
  }

}
