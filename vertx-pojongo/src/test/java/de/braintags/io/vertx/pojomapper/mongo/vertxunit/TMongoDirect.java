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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.mongo.MongoUtil;
import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.testdatastore.ResultContainer;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.MiniMapper;
import de.braintags.io.vertx.util.CounterObject;
import io.vertx.core.json.JsonArray;
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
  private static final int LOOP = 500;
  private static final String EXPECTED_VERSION_STARTS_WITH = "3.";

  @Test
  public void checkVersion(TestContext context) {
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    Async async = context.async();
    ds.getMetaData().getVersion(res -> {
      if (res.failed()) {
        context.fail(res.cause());
      } else {
        LOGGER.info("VERSION: " + res.result());
        context.assertTrue(res.result().startsWith(EXPECTED_VERSION_STARTS_WITH),
            "Mongo version must start with " + EXPECTED_VERSION_STARTS_WITH + " | CURRENT: " + res.result());
      }
      async.complete();
    });
    async.await();
  }

  @Test
  public void executeNativeQuery(TestContext context) {
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    clearTable(context, MiniMapper.class);
    List<MiniMapper> write = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      write.add(new MiniMapper("native " + i));
    }
    saveRecords(context, write);
    IQuery<MiniMapper> query = ds.createQuery(MiniMapper.class);
    String qs = "{\"name\":{\"$regex\":\".*native.*\"}}";
    JsonObject json = new JsonObject(qs);
    query.setNativeCommand(json);

    ResultContainer resultContainer = find(context, query, 10);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  @Test
  public void testWrongNativeFormat(TestContext context) {
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    IQuery<MiniMapper> query = ds.createQuery(MiniMapper.class);
    query.setNativeCommand(new String());
    ResultContainer resultContainer = find(context, query, -1);
    if (resultContainer.assertionError == null)
      context.fail("Expected an exception here");
  }

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
  public void massInsert(TestContext context) {
    LOGGER.info("-->>test");
    Async async = context.async();
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    MongoClient client = (MongoClient) ds.getClient();
    CounterObject co = new CounterObject<>(LOOP, null);
    for (int i = 0; i < LOOP; i++) {
      JsonObject jsonCommand = new JsonObject().put("name", "testName " + i);
      client.insert("massInsert", jsonCommand, result -> {
        if (result.failed()) {
          co.setThrowable(result.cause());
          context.fail(result.cause());
          LOGGER.error("", result.cause());
          async.complete();
        } else {
          LOGGER.info("executed: " + result.result());
          if (co.reduce()) {
            async.complete();
          }
        }
      });
      if (co.isError()) {
        break;
      }
    }
    async.await();
  }

  @Test
  public void simpleTest(TestContext context) {
    LOGGER.info("-->>test");
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    MongoClient client = (MongoClient) ds.getClient();
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
    MongoClient client = (MongoClient) ds.getClient();

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
    MongoClient client = (MongoClient) ds.getClient();
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
    MongoClient client = (MongoClient) ds.getClient();

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
  public void listCollectionNames(TestContext context) {
    Async as = context.async();
    MongoUtil.getCollectionNames((MongoDataStore) getDataStore(context), result -> {
      if (result.failed()) {
        context.fail(new RuntimeException(result.cause()));
        as.complete();
      } else {
        LOGGER.info("success: " + result.result());
        as.complete();
      }
    });
    as.await();
  }

  @Test
  public void testIndexes(TestContext context) {
    String collectionName = "XXCollection";
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    MongoClient client = (MongoClient) ds.getClient();
    dropTable(context, collectionName);
    listCommands(context, client);
    createCollection(context, collectionName);
    createIndex(context, client, collectionName);
    listIndexes(context, collectionName);
  }

  @Test
  public void listCollections(TestContext context) {
    Async as = context.async();
    MongoUtil.getCollections((MongoDataStore) getDataStore(context), result -> {
      if (result.failed()) {
        context.fail(new RuntimeException(result.cause()));
        as.complete();
      } else {
        LOGGER.info("success: " + result.result());
        as.complete();
      }
    });
    as.await();
  }

  /**
   * 
   * @param context
   * @param client
   * @param collection
   */
  private void listIndexes(TestContext context, String collection) {
    Async as = context.async();
    MongoUtil.getIndexes((MongoDataStore) getDataStore(context), collection, result -> {
      if (result.failed()) {
        context.fail(new RuntimeException(result.cause()));
        as.complete();
      } else {
        LOGGER.info("success: " + result.result());
        as.complete();
      }
    });
    as.await();
  }

  private void listCommands(TestContext context, MongoClient client) {
    Async as = context.async();
    JsonObject jsonCommand = new JsonObject();
    jsonCommand.put("listCommands", 1);
    client.runCommand("listCommands", jsonCommand, result -> {
      if (result.failed()) {
        context.fail(new RuntimeException(result.cause()));
        as.complete();
      } else {
        LOGGER.info("success: " + result.result());
        as.complete();
      }
    });
    as.await();
  }

  private void createCollection(TestContext context, String collection) {
    Async as = context.async();
    MongoUtil.createCollection((MongoDataStore) getDataStore(context), collection, result -> {
      if (result.failed()) {
        context.fail(new RuntimeException(result.cause()));
        as.complete();
      } else {
        LOGGER.info("success: " + result.result());
        as.complete();
      }
    });
    as.await();
  }

  private void createIndex(TestContext context, MongoClient client, String collection) {
    Async as = context.async();
    JsonObject jsonCommand = new JsonObject();
    jsonCommand.put("createIndexes", collection);
    JsonArray idx = new JsonArray();
    idx.add(createIndexDefinition("testindex", "testFieldName"));
    jsonCommand.put("indexes", idx);

    client.runCommand("createIndexes", jsonCommand, result -> {
      if (result.failed()) {
        context.fail(new RuntimeException(result.cause()));
        as.complete();
      } else {
        LOGGER.info("success: " + result.result());
        as.complete();
      }
    });
    as.await();
  }

  private JsonObject createIndexDefinition(String indexName, String fieldName) {
    JsonObject idxObject = new JsonObject();
    idxObject.put("name", indexName);
    JsonObject fieldDefs = new JsonObject();
    fieldDefs.put(fieldName, 1);
    idxObject.put("key", fieldDefs);
    return idxObject;
  }

}
