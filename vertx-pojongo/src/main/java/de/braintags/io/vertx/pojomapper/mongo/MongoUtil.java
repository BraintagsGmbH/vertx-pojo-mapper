package de.braintags.io.vertx.pojomapper.mongo;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.annotation.Index;
import de.braintags.io.vertx.pojomapper.annotation.IndexField;
import de.braintags.io.vertx.pojomapper.annotation.IndexOptions;
import de.braintags.io.vertx.pojomapper.annotation.Indexes;
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
  public static final void getCollections(MongoDataStore ds, Handler<AsyncResult<JsonObject>> result) {
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
  public static final void getCollectionNames(MongoDataStore ds, Handler<AsyncResult<List<String>>> result) {
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
  public static void createCollection(MongoDataStore ds, String collection, Handler<AsyncResult<JsonObject>> handler) {
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
  public static void getIndexes(MongoDataStore ds, String collection, Handler<AsyncResult<JsonObject>> handler) {
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
  public static final void getIndexNames(MongoDataStore ds, String collection,
      Handler<AsyncResult<List<String>>> result) {
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

  public static final void createIndexes(MongoDataStore ds, String collection, Indexes indexes,
      Handler<AsyncResult<JsonObject>> handler) {
    try {
      JsonObject indexCommand = new JsonObject().put("createIndexes", collection);
      JsonArray idx = new JsonArray();
      for (Index index : indexes.value()) {
        idx.add(createIndexDefinition(index));
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

  private static JsonObject createIndexDefinition(Index index) {
    JsonObject idxObject = new JsonObject();
    idxObject.put("name", index.name());
    JsonObject keyObject = new JsonObject();
    idxObject.put("key", keyObject);

    for (IndexField field : index.fields()) {
      addIndexField(keyObject, field);
    }
    addIndexOptions(idxObject, index.options());
    return idxObject;
  }

  private static void addIndexField(JsonObject keyObject, IndexField field) {
    keyObject.put(field.fieldName(), field.type().toIndexValue());
  }

  private static void addIndexOptions(JsonObject indexDef, IndexOptions options) {
    indexDef.put("unique", options.unique());
  }
}
