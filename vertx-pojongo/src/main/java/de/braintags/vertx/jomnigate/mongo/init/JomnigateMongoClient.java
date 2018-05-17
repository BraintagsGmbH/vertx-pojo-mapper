package de.braintags.vertx.jomnigate.mongo.init;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.conversions.Bson;

import com.mongodb.async.client.MongoCollection;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.MongoClientImpl;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;

public class JomnigateMongoClient extends MongoClientImpl {

  private final String database;
  private final Vertx vertx;

  JomnigateMongoClient(final Vertx vertx, final JsonObject config, final String dataSourceName) {
    super(vertx, config, dataSourceName);
    this.vertx = vertx;
    MongoClientOptionsParser parser = new MongoClientOptionsParser(config);
    database = parser.database();
  }

  public MongoCollection<JsonObject> getCollection(final String name) {
    MongoCollection<JsonObject> coll = mongo.getDatabase(database).getCollection(name, JsonObject.class);
    return coll;
  }

  /**
   * Create a Mongo client which maintains its own data source.
   *
   * @param vertx
   *          the Vert.x instance
   * @param config
   *          the configuration
   * @return the client
   */
  static JomnigateMongoClient createNonShared(final Vertx vertx, final JsonObject config) {
    return new JomnigateMongoClient(vertx, config, UUID.randomUUID().toString());
  }

  /**
   * Create a Mongo client which shares its data source with any other Mongo clients created with the same
   * data source name
   *
   * @param vertx
   *          the Vert.x instance
   * @param config
   *          the configuration
   * @param dataSourceName
   *          the data source name
   * @return the client
   */
  static JomnigateMongoClient createShared(final Vertx vertx, final JsonObject config, final String dataSourceName) {
    return new JomnigateMongoClient(vertx, config, dataSourceName);
  }

  /**
   * Like {@link #createShared(io.vertx.core.Vertx, JsonObject, String)} but with the default data source name
   *
   * @param vertx
   *          the Vert.x instance
   * @param config
   *          the configuration
   * @return the client
   */
  static JomnigateMongoClient createShared(final Vertx vertx, final JsonObject config) {
    return new JomnigateMongoClient(vertx, config, DEFAULT_POOL_NAME);
  }

  public Future<List<JsonObject>> aggregateOnCollection(final String collectionName, final List<Bson> pipeLine) {
    Future<List<JsonObject>> f = Future.future();
    Context context = vertx.getOrCreateContext();
    List<JsonObject> resultList = new ArrayList<>();
    getCollection(collectionName).aggregate(pipeLine, JsonObject.class).into(resultList,
        (result, error) -> context.runOnContext(v -> {
          if (error != null) {
            f.fail(error);
          } else {
            f.complete(resultList);
          }
        }));
    return f;
  }

}
