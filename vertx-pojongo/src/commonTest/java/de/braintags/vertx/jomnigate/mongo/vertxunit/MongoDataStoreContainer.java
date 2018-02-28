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

import java.util.HashMap;
import java.util.Map;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.init.IDataStoreInit;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ArrayTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.json.typehandler.handler.CollectionTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.json.typehandler.handler.MapTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ObjectTypeHandler;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ObjectTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.mapping.IIndexDefinition;
import de.braintags.vertx.jomnigate.mapping.IIndexFieldDefinition;
import de.braintags.vertx.jomnigate.mapping.IKeyGenerator;
import de.braintags.vertx.jomnigate.mapping.IndexOption;
import de.braintags.vertx.jomnigate.mongo.MongoDataStore;
import de.braintags.vertx.jomnigate.mongo.init.MongoDataStoreInit;
import de.braintags.vertx.jomnigate.testdatastore.AbstractDataStoreContainer;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.AbstractTypeHandlerTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.ArrayTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.BooleanTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.CalendarTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.CollectionTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.DateTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.EmbeddedArrayTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.EmbeddedListTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.EmbeddedMapTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.EmbeddedSingleTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.EmbeddedSingleTest_Null;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.EnumTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.JsonTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.LocaleTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.MapTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.MiscTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.NumericTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.PriceTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.PropertiesTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.ReferencedArrayTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.ReferencedListTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.ReferencedMapTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.ReferencedSingleTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.StringTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.geo.GeoPointTest;
import de.braintags.vertx.jomnigate.typehandler.stringbased.handlers.JsonTypeHandler;
import de.braintags.vertx.util.exception.InitException;
import de.flapdoodle.embed.mongo.MongodExecutable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.TestContext;

/**
 *
 *
 * @author Michael Remme
 *
 */
public class MongoDataStoreContainer extends AbstractDataStoreContainer {
  private static final io.vertx.core.logging.Logger logger = io.vertx.core.logging.LoggerFactory
      .getLogger(MongoDataStoreContainer.class);

  private static final int LOCAL_PORT = 27017;
  private static final String START_MONGO_LOCAL_PROP = "startMongoLocal";
  public static final String CONNECTION_STRING_PROPERTY = "connection_string";
  public static final String DEFAULT_CONNECTION = "mongodb://localhost:27017";
  private static boolean handleReferencedRecursive = true;

  private static MongodExecutable exe;
  private MongoDataStore mongoDataStore;
  private final Map<String, String> thMap = new HashMap<>();

  /**
   *
   */
  public MongoDataStoreContainer() {
    thMap.put(StringTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(NumericTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(PriceTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(MiscTest.class.getName(), ObjectTypeHandler.class.getName());

    thMap.put(EnumTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(GeoPointTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(LocaleTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(JsonTest.class.getName(), ObjectTypeHandler.class.getName());

    thMap.put(BooleanTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(DateTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(CalendarTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(JsonTest.class.getName(), JsonTypeHandler.class.getName());
    thMap.put(PropertiesTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(MapTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(ArrayTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(CollectionTest.class.getName(), ObjectTypeHandler.class.getName());

    thMap.put(GeoPointTest.class.getName(), ObjectTypeHandler.class.getName());

    thMap.put(EmbeddedListTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(EmbeddedMapTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(EmbeddedArrayTest.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(EmbeddedSingleTest_Null.class.getName(), ObjectTypeHandler.class.getName());
    thMap.put(EmbeddedSingleTest.class.getName(), ObjectTypeHandler.class.getName());

    thMap.put(ReferencedSingleTest.class.getName(), ObjectTypeHandlerReferenced.class.getName());
    thMap.put(ReferencedArrayTest.class.getName(), ArrayTypeHandlerReferenced.class.getName());
    thMap.put(ReferencedListTest.class.getName(), CollectionTypeHandlerReferenced.class.getName());
    thMap.put(ReferencedMapTest.class.getName(), MapTypeHandlerReferenced.class.getName());
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.datastoretest.IDatastoreContainer#startup(io.vertx.core.Vertx,
   * io.vertx.core.Handler)
   */
  @Override
  public void startup(final Vertx vertx, final Handler<AsyncResult<Void>> handler) {
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
            exe = ((MongoDataStoreInit) dsInit).getMongodExecutable();
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

  @Override
  public DataStoreSettings createSettings() {
    DataStoreSettings settings = MongoDataStoreInit.createDefaultSettings();
    settings.setDatabaseName("UnitTestDatabase");
    settings.getProperties().put(MongoDataStoreInit.CONNECTION_STRING_PROPERTY,
        getProperty(CONNECTION_STRING_PROPERTY, DEFAULT_CONNECTION));
    settings.getProperties().put(MongoDataStoreInit.START_MONGO_LOCAL_PROP,
        getProperty(START_MONGO_LOCAL_PROP, "false"));
    settings.getProperties().put(MongoDataStoreInit.LOCAL_PORT_PROP, String.valueOf(LOCAL_PORT));
    settings.getProperties().put(MongoDataStoreInit.SHARED_PROP, "false");
    settings.getProperties().put(MongoDataStoreInit.HANDLE_REFERENCED_RECURSIVE_PROP,
        String.valueOf(handleReferencedRecursive));
    if (DEFAULT_KEY_GENERATOR != null) {
      settings.getProperties().put(IKeyGenerator.DEFAULT_KEY_GENERATOR, DEFAULT_KEY_GENERATOR);
    } else {
      settings.getProperties().remove(IKeyGenerator.DEFAULT_KEY_GENERATOR);
    }
    return settings;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.datastoretest.IDatastoreContainer#getDataStore()
   */
  @Override
  public IDataStore getDataStore() {
    return mongoDataStore;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.datastoretest.IDatastoreContainer#shutdown(io.vertx.core.Handler)
   */
  @Override
  public void shutdown(final Handler<AsyncResult<Void>> handler) {
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
   * @see de.braintags.vertx.jomnigate.datastoretest.IDatastoreContainer#dropTable(java.lang.String,
   * io.vertx.core.Handler)
   */
  @Override
  public void dropTable(final String collection, final Handler<AsyncResult<Void>> handler) {
    logger.info("DROPPING: " + collection);
    ((MongoClient) mongoDataStore.getClient()).dropCollection(collection, dropResult -> {
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
   * @see de.braintags.vertx.jomnigate.testdatastore.IDatastoreContainer#clearTable(java.lang.String,
   * io.vertx.core.Handler)
   */
  @Override
  public void clearTable(final String tablename, final Handler<AsyncResult<Void>> handler) {
    dropTable(tablename, handler);
  }

  /**
   * Get a property with the given key
   *
   * @param name
   *          the key of the property to be fetched
   * @return a valid value or null
   */
  private static String getProperty(final String name, final String defaultValue) {
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
  public String getExpectedTypehandlerName(final Class<? extends AbstractTypeHandlerTest> testClass,
      final String defaultName) {
    if (thMap.containsKey(testClass.getName()))
      return thMap.get(testClass.getName());
    return defaultName;
  }

  @Override
  public void checkIndex(final Object indexInfo, final IIndexDefinition sourceIndex, final TestContext context) {
    context.assertTrue(indexInfo instanceof JsonObject);
    JsonObject index = (JsonObject) indexInfo;
    context.assertEquals(sourceIndex.getName(), index.getString("name"));
    JsonObject key = index.getJsonObject("key");
    for (IIndexFieldDefinition field : sourceIndex.getFields()) {
      Object indexValue = field.getType().toIndexValue();
      switch (field.getType()) {
        case ASC:
        case DESC:
          context.assertEquals(indexValue, key.getInteger(field.getName()));
          break;
        case TEXT:
          context.assertEquals(indexValue, key.getString("_fts"));
          JsonObject weights = index.getJsonObject("weights");
          context.assertEquals(1, weights.getInteger(field.getName()));
          break;
        case GEO2D:
        case GEO2DSPHERE:
          context.assertEquals(indexValue, key.getString("position"));
          break;
        case HASHED:
          context.assertEquals(indexValue, key.getString("name"));
          break;
        default:
          context.fail("Unknown index type: " + field.getType());
          return;
      }
    }

    for (IndexOption option : sourceIndex.getIndexOptions()) {
      switch (option.getFeature()) {
        case UNIQUE:
          boolean unique = (boolean) option.getValue();
          if (unique)
            context.assertEquals(true, index.getBoolean("unique"));
          break;
        case PARTIAL_FILTER_EXPRESSION:
          // can not easily assert that the query matches the source definition as it is transformed to a mongoDB query
          context.assertNotNull(index.getJsonObject("partialFilterExpression"));
          break;
        default:
          context.fail("Unknown index option: " + option.getFeature());
          return;
      }
    }
  }

}
