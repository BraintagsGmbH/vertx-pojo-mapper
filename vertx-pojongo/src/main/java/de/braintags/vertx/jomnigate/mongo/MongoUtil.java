/*-
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
package de.braintags.vertx.jomnigate.mongo;

import java.util.ArrayList;
import java.util.List;

import de.braintags.vertx.jomnigate.annotation.Index;
import de.braintags.vertx.jomnigate.mapping.IIndexDefinition;
import de.braintags.vertx.jomnigate.mapping.IIndexFieldDefinition;
import de.braintags.vertx.jomnigate.mapping.IndexOption;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Utility class for MongoDb
 * 
 * @author Michael Remme
 * 
 */
public final class MongoUtil {

  private MongoUtil() {

  }

  /**
   * Request for existing collections
   * 
   * @param ds
   *          the datastore to be used
   * @param result
   *          returns the complete result with detailed information of every collection
   */
  public static final void getCollections(final MongoDataStore ds, final Handler<AsyncResult<JsonObject>> result) {
    JsonObject jsonCommand = new JsonObject().put("listCollections", 1);
    ((MongoClient) ds.getClient()).runCommand("listCollections", jsonCommand, result);
  }

  /**
   * Request for existing collections as names
   * 
   * @param ds
   *          the datastore to be used
   * @param result
   *          returns the complete result with detailed information of every collection
   */
  public static final void getCollectionNames(final MongoDataStore ds, final Handler<AsyncResult<List<String>>> result) {
    getCollections(ds, res -> {
      if (res.failed()) {
        result.handle(Future.failedFuture(res.cause()));
      } else {
        List<String> returnList = new ArrayList<>();
        JsonArray array = res.result().getJsonObject("cursor").getJsonArray("firstBatch");
        array.forEach(jo -> returnList.add(((JsonObject) jo).getString("name")));
        result.handle(Future.succeededFuture(returnList));
      }
    });
  }

  /**
   * Call to explicitly create a collection inside the database
   * 
   * @param ds
   * @param collection
   * @param handler
   */
  public static void createCollection(final MongoDataStore ds, final String collection, final Handler<AsyncResult<JsonObject>> handler) {
    JsonObject jsonCommand = new JsonObject().put("create", collection);
    ((MongoClient) ds.getClient()).runCommand("create", jsonCommand, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(new RuntimeException(result.cause())));
      } else {
        handler.handle(result);
      }
    });
  }

  /**
   * List all existing indexes for the given collection
   * 
   * @param ds
   * @param collection
   * @param handler
   */
  public static void getIndexes(final MongoDataStore ds, final String collection, final Handler<AsyncResult<JsonObject>> handler) {
    JsonObject jsonCommand = new JsonObject().put("listIndexes", collection);
    ((MongoClient) ds.getClient()).runCommand("listIndexes", jsonCommand, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(new RuntimeException(result.cause())));
      } else {
        handler.handle(result);
      }
    });
  }

  /**
   * Request for existing indexes as names
   * 
   * @param ds
   * @param collection
   * @param handler
   */
  public static final void getIndexNames(final MongoDataStore ds, final String collection,
      final Handler<AsyncResult<List<String>>> result) {
    getIndexes(ds, collection, res -> {
      if (res.failed()) {
        result.handle(Future.failedFuture(res.cause()));
      } else {
        List<String> returnList = new ArrayList<>();
        JsonArray array = res.result().getJsonObject("cursor").getJsonArray("firstBatch");
        array.forEach(jo -> returnList.add(((JsonObject) jo).getString("name")));
        result.handle(Future.succeededFuture(returnList));
      }
    });
  }

  /**
   * Create indexes which are defined by the given {@link Index}
   * 
   * @param ds
   *          the datastore
   * @param collection
   *          the name of the collection to be used
   * @param indexDefinitions
   *          the index definition
   * @param handler
   *          the handler to be informed with the result of the index creation
   */
  public static final void createIndexes(final MongoDataStore ds, final String collection,
      final List<IIndexDefinition> indexDefinitions,
      final Handler<AsyncResult<JsonObject>> handler) {
    try {
      JsonObject indexCommand = new JsonObject().put("createIndexes", collection);
      JsonArray idx = new JsonArray();
      for (IIndexDefinition indexDefinition : indexDefinitions) {
        idx.add(createIndexDefinition(indexDefinition));
      }
      indexCommand.put("indexes", idx);
      ((MongoClient) ds.getClient()).runCommand("createIndexes", indexCommand, result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(new RuntimeException(result.cause())));
        } else {
          handler.handle(result);
        }
      });
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  private static JsonObject createIndexDefinition(final IIndexDefinition indexDefinition) {
    JsonObject idxObject = new JsonObject();
    idxObject.put("name", indexDefinition.getName());
    JsonObject keyObject = new JsonObject();
    idxObject.put("key", keyObject);

    for (IIndexFieldDefinition field : indexDefinition.getFields()) {
      addIndexField(keyObject, field);
    }
    addIndexOptions(idxObject, indexDefinition.getIndexOptions());
    return idxObject;
  }

  private static void addIndexField(final JsonObject keyObject, final IIndexFieldDefinition field) {
    keyObject.put(field.getName(), field.getType().toIndexValue());
  }

  private static void addIndexOptions(final JsonObject indexDef,
      final List<de.braintags.vertx.jomnigate.mapping.IndexOption> indexOptions) {
    for (IndexOption option : indexOptions) {
      switch (option.getFeature()) {
      case UNIQUE:
        indexDef.put("unique", option.getValue());  
        break;
      default:
        throw new IllegalArgumentException("Unknown IndexFeature: " + option.getFeature());
      }
    }
  }
}
