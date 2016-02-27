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

  // @Test
  // public void dateTest(TestContext context) {
  // MongoDataStore ds = (MongoDataStore) getDataStore(context);
  // MongoClient client = ds.getMongoClient();
  // JsonObject jsonCommand = new JsonObject();
  // // getNextSequenceValue("productid")
  // // jsonCommand.put("_id", "getNextSequenceValue(\"productid\")".getBytes());
  // jsonCommand.put("Datum1", new Timestamp(System.currentTimeMillis()));
  // client.insert("DateTest", jsonCommand, result -> {
  // if (result.failed()) {
  // context.fail(result.cause());
  // LOGGER.error("", result.cause());
  // } else {
  // LOGGER.info("executed: " + result.result());
  // }
  // });
  // }

  @Test
  public void simpleTest(TestContext context) {
    LOGGER.info("-->>test");
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
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
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
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

  /**
   * Creats a collection with a nummeric value and executes findAndModify to raise and return the numeric value inside
   * one request to perform a sequence
   * 
   * @param context
   */
  @Test
  public void createSequence(TestContext context) {
    Async as = context.async();
    String collection = "SequenceTest";
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    MongoClient client = ds.getMongoClient();
    clearTable(context, collection);

    JsonObject jsonCommand = new JsonObject();
    jsonCommand.put("sequence", 1);
    client.insert(collection, jsonCommand, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
        as.complete();
      } else {
        LOGGER.info("executed: " + result.result());
        JsonObject query = new JsonObject();
        query.put("_id", result.result());
        JsonObject execComnand = createSequenceCommand(collection, query, "sequence");
        client.runCommand("findAndModify", execComnand, ur -> {
          if (ur.failed()) {
            LOGGER.error("", ur.cause());
            context.fail(ur.cause());
            as.complete();
          } else {
            LOGGER.info("success");
            LOGGER.info("RESULT" + ur.result());
            JsonObject resJo = ur.result();
            JsonObject value = resJo.getJsonObject("value");
            int seq = value.getInteger("sequence");
            context.assertEquals(2, seq, "the sequence is wrong");
            as.complete();
          }
        });
      }
    });
    as.await();
  }

  private JsonObject createSequenceCommand(String collection, JsonObject query, String sequenceField) {
    JsonObject updateCommand = new JsonObject().put("$inc", new JsonObject().put(sequenceField, 1));
    return createFindAndModify(collection, query, updateCommand);
  }

  /*
   * {
   * findAndModify: <collection-name>,
   * query: <document>,
   * sort: <document>,
   * remove: <boolean>,
   * update: <document>,
   * new: <boolean>,
   * fields: <document>,
   * upsert: <boolean>,
   * bypassDocumentValidation: <boolean>,
   * writeConcern: <document>
   * }
   */
  private JsonObject createFindAndModify(String collection, JsonObject query, JsonObject updateCommand) {
    JsonObject retOb = new JsonObject();
    retOb.put("findAndModify", collection);
    retOb.put("query", query);
    retOb.put("update", updateCommand);
    retOb.put("new", true);
    return retOb;
  }

  @Test
  public void testUpdateWithSave(TestContext context) {
    Async as = context.async();
    String collection = "UpdateTestCollection";
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
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
