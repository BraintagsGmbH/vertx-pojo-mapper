/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mongo.vertxunit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.mongo.MongoDataStore;
import de.braintags.vertx.jomnigate.mongo.MongoUtil;
import de.braintags.vertx.jomnigate.testdatastore.DatastoreBaseTest;
import de.braintags.vertx.jomnigate.testdatastore.ResultContainer;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapper;
import de.braintags.vertx.util.ErrorObject;
import de.braintags.vertx.util.ResultObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.FindOptions;
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

  private List<JsonObject> handleResult(MongoClient client, AsyncResult<List<JsonObject>> seachResult)
      throws Throwable {
    CountDownLatch latch = new CountDownLatch(1);
    ResultObject<List<JsonObject>> ro = new ResultObject(null);
    FindOptions fo = new FindOptions().setLimit(5000);
    client.findWithOptions("MiniMapper", new JsonObject(), fo, result -> {
      LOGGER.info("query executed");
      try {
        ro.setResult(handleResult(client, result));
      } catch (Throwable e) {
        ro.setThrowable(e);
      }
      latch.countDown();
    });

    latch.await();

    if (ro.isError()) {
      throw ro.getRuntimeException();
    } else {
      return ro.getResult();
    }
  }

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
    LOGGER.info("executing executeNativeQuery");
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    clearTable(context, MiniMapper.class);
    List<MiniMapper> write = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      write.add(new MiniMapper("native " + i));
    }
    LOGGER.info("saving records");
    saveRecords(context, write);
    IQuery<MiniMapper> query = ds.createQuery(MiniMapper.class);
    String qs = "{\"name\":{\"$regex\":\".*native.*\"}}";
    JsonObject json = new JsonObject(qs);
    query.setNativeCommand(json);

    ResultContainer resultContainer = find(context, query, 10);
  }

  @Test
  public void testWrongNativeFormat(TestContext context) {
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    IQuery<MiniMapper> query = ds.createQuery(MiniMapper.class);
    query.setNativeCommand(new String());
    try {
      ResultContainer resultContainer = find(context, query, -1);
      context.fail("Expected an exception here");
    } catch (Throwable e) {
      // all fine - expected
    }
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
    String COLLECTION = "massInsert";
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    MongoClient client = (MongoClient) ds.getClient();
    dropTable(context, COLLECTION);
    List<Future> fl = new ArrayList<>();
    for (int i = 0; i < LOOP; i++) {
      Future<String> f = Future.future();
      fl.add(f);
      JsonObject jsonCommand = new JsonObject().put("name", "testName " + i);
      client.insert(COLLECTION, jsonCommand, f.completer());
    }

    ResultObject<List> ro = new ResultObject<>(null);
    Async async = context.async();
    CompositeFuture cf = CompositeFuture.all(fl);
    cf.setHandler(cfr -> {
      if (cfr.failed()) {
        ro.setThrowable(cfr.cause());
        async.complete();
      } else {
        LOGGER.info("number of saved records: " + cf.list().size());
        ro.setResult(cf.list());

        async.complete();
      }
    });

    async.await();
    if (ro.isError()) {
      LOGGER.error("", ro.getThrowable());
      context.fail(ro.getThrowable());
    } else {
      context.assertEquals(LOOP, ro.getResult().size());
      client.count(COLLECTION, new JsonObject(), fr -> {
        if (fr.failed()) {
          LOGGER.error("", fr.cause());
          context.fail(fr.cause());
        } else {
          context.assertEquals(LOOP, fr.result().intValue());
        }
      });

    }
  }

  @Test
  public void simpleInsert(TestContext context) {
    ErrorObject err = new ErrorObject<>(null);
    Async async = context.async();
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    MongoClient client = (MongoClient) ds.getClient();
    JsonObject jsonCommand = new JsonObject();
    jsonCommand.put("name", "testName");
    client.insert("nativeCommandCollection", jsonCommand, result -> {
      if (result.failed()) {
        err.setThrowable(result.cause());
        LOGGER.error("", result.cause());
        async.complete();
      } else {
        LOGGER.info("executed: " + result.result());
        try {
          context.assertNotNull(result.result(), "no identifyer returned");
        } catch (Exception e) {
          err.setThrowable(e);
        } finally {
          async.complete();
        }
      }
    });
    async.await();
    if (err.isError()) {
      context.fail(err.getThrowable());
    }
  }

  /**
   * Insert a record, where the field _id is specified, but as null
   * 
   * @param context
   */
  @Test
  public void insertWithIdNull(TestContext context) {
    ErrorObject err = new ErrorObject<>(null);
    Async async = context.async();
    LOGGER.info("-->>test");
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    MongoClient client = (MongoClient) ds.getClient();
    dropTable(context, "nativeCommandCollection");

    JsonObject jsonCommand = new JsonObject("{\"name\":\"testName\",\"_id\":null}");
    client.insert("nativeCommandCollection", jsonCommand, result -> {
      if (result.failed()) {
        err.setThrowable(result.cause());
        LOGGER.error("", result.cause());
        async.complete();
      } else {
        LOGGER.info("executed: " + result.result());
        try {
          context.assertNull(result.result(), "This should result in NULL");
        } catch (Throwable e) {
          err.setThrowable(e);
        } finally {
          async.complete();
        }
      }
    });
    async.await();
    if (err.isError()) {
      context.fail(err.getThrowable());
    }
  }

  //

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
