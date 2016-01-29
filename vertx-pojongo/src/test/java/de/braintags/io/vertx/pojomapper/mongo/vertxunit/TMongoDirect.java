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

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class TMongoDirect extends DatastoreBaseTest {
  private static Logger LOGGER = LoggerFactory.getLogger(TMongoDirect.class);

  @Test
  public void simpleTest(TestContext context) {
    LOGGER.info("-->>test");
    MongoDataStore ds = (MongoDataStore) getDataStore();
    MongoClient client = ds.getMongoClient();
    JsonObject jsonCommand = new JsonObject();
    // getNextSequenceValue("productid")
    // jsonCommand.put("_id", "getNextSequenceValue(\"productid\")".getBytes());
    jsonCommand.put("name", "testName");
    client.insert("nativeCommandCollection", jsonCommand, result -> {
      if (result.failed()) {
        context.fail(result.cause());
        LOGGER.error("", result.cause());
      } else {
        LOGGER.info("executed: " + result.result());
      }
    });
  }

  @Test
  public void testUpdate(TestContext context) {
    Async as = context.async();
    String collection = "UpdateTestCollection";
    MongoDataStore ds = (MongoDataStore) getDataStore();
    MongoClient client = ds.getMongoClient();

    JsonObject insertCommand = new JsonObject();
    insertCommand.put("name", "testName");
    JsonObject updateCommand = new JsonObject().put("$set", new JsonObject().put("name", "modified name"));
    client.insert(collection, insertCommand, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
        context.fail(result.cause());
        as.complete();
      } else {
        LOGGER.info("executed: " + result.result());
        JsonObject query = new JsonObject();
        query.put("_id", result.result());
        client.update(collection, query, updateCommand, ur -> {
          if (ur.failed()) {
            LOGGER.error("", ur.cause());
            context.fail(ur.cause());
            as.complete();
          } else {
            LOGGER.info("success");
            as.complete();
          }
        });
      }
    });
    as.await();
  }

  // "$inc"
  @Test
  public void testUpdateWithInc(TestContext context) {
    Async as = context.async();
    String collection = "UpdateTestCollection";
    MongoDataStore ds = (MongoDataStore) getDataStore();
    MongoClient client = ds.getMongoClient();

    JsonObject jsonCommand = new JsonObject();
    jsonCommand.put("name", "testName");
    client.insert(collection, jsonCommand, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
        as.complete();
      } else {
        LOGGER.info("executed: " + result.result());
        JsonObject query = new JsonObject();
        query.put("_id", result.result());
        jsonCommand.put("name", "modified name");
        client.save(collection, jsonCommand, ur -> {
          if (ur.failed()) {
            LOGGER.error("", ur.cause());
            as.complete();
          } else {
            LOGGER.info("success");
            as.complete();
          }
        });
      }
    });
    as.await();
  }

  @Test
  public void testUpdateWithSave(TestContext context) {
    Async as = context.async();
    String collection = "UpdateTestCollection";
    MongoDataStore ds = (MongoDataStore) getDataStore();
    MongoClient client = ds.getMongoClient();

    JsonObject jsonCommand = new JsonObject();
    jsonCommand.put("name", "testName");
    client.insert(collection, jsonCommand, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
        as.complete();
      } else {
        LOGGER.info("executed: " + result.result());
        JsonObject query = new JsonObject();
        query.put("_id", result.result());
        jsonCommand.put("name", "modified name");
        client.save(collection, jsonCommand, ur -> {
          if (ur.failed()) {
            LOGGER.error("", ur.cause());
            as.complete();
          } else {
            LOGGER.info("success");
            as.complete();
          }
        });
      }
    });
    as.await();
  }

}
