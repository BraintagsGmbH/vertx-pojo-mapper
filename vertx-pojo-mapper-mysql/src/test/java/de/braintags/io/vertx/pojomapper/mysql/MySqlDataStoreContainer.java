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

package de.braintags.io.vertx.pojomapper.mysql;

import java.util.HashMap;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.init.DataStoreSettings;
import de.braintags.io.vertx.pojomapper.init.IDataStoreInit;
import de.braintags.io.vertx.pojomapper.mysql.init.MySqlDataStoreinit;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.BooleanTypeHandler;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlArrayTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlArrayTypeHandlerReferenced;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlArrayTypehandler;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlCalendarTypehandler;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlCollectionTypeHandler;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlCollectionTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlCollectionTypeHandlerReferenced;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlDateTypeHandler;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlGeoPointTypeHandler;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlMapTypeHandler;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlMapTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlMapTypeHandlerReferenced;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlObjectTypehandlerEmbedded;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlObjectTypehandlerReferenced;
import de.braintags.io.vertx.pojomapper.testdatastore.AbstractDataStoreContainer;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.AbstractTypeHandlerTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.ArrayTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.BooleanTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.CalendarTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.CollectionTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.DateTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.EmbeddedArrayTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.EmbeddedListTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.EmbeddedMapTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.EmbeddedSingleTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.EmbeddedSingleTest_Null;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.JsonTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.MapTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.PropertiesTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.ReferencedArrayTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.ReferencedListTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.ReferencedMapTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.ReferencedSingleTest;
import de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json.geo.GeoPointTest;
import de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.JsonTypeHandler;
import de.braintags.io.vertx.util.exception.InitException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * 
 * @author Michael Remme
 * 
 */

public class MySqlDataStoreContainer extends AbstractDataStoreContainer {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(MySqlDataStoreContainer.class);

  private MySqlDataStore datastore;
  private Map<String, String> thMap = new HashMap<>();

  /**
   * 
   */
  public MySqlDataStoreContainer() {
    thMap.put(BooleanTest.class.getName(), BooleanTypeHandler.class.getName());
    thMap.put(DateTest.class.getName(), SqlDateTypeHandler.class.getName());
    thMap.put(CalendarTest.class.getName(), SqlCalendarTypehandler.class.getName());
    thMap.put(JsonTest.class.getName(), JsonTypeHandler.class.getName());
    thMap.put(PropertiesTest.class.getName(), SqlMapTypeHandler.class.getName());
    thMap.put(MapTest.class.getName(), SqlMapTypeHandler.class.getName());
    thMap.put(ArrayTest.class.getName(), SqlArrayTypehandler.class.getName());
    thMap.put(CollectionTest.class.getName(), SqlCollectionTypeHandler.class.getName());
    thMap.put(EmbeddedListTest.class.getName(), SqlCollectionTypeHandlerEmbedded.class.getName());
    thMap.put(EmbeddedMapTest.class.getName(), SqlMapTypeHandlerEmbedded.class.getName());
    thMap.put(EmbeddedArrayTest.class.getName(), SqlArrayTypeHandlerEmbedded.class.getName());
    thMap.put(EmbeddedSingleTest_Null.class.getName(), SqlObjectTypehandlerEmbedded.class.getName());
    thMap.put(EmbeddedSingleTest.class.getName(), SqlObjectTypehandlerEmbedded.class.getName());
    thMap.put(ReferencedSingleTest.class.getName(), SqlObjectTypehandlerReferenced.class.getName());
    thMap.put(ReferencedArrayTest.class.getName(), SqlArrayTypeHandlerReferenced.class.getName());
    thMap.put(ReferencedListTest.class.getName(), SqlCollectionTypeHandlerReferenced.class.getName());
    thMap.put(ReferencedMapTest.class.getName(), SqlMapTypeHandlerReferenced.class.getName());
    thMap.put(GeoPointTest.class.getName(), SqlGeoPointTypeHandler.class.getName());
  }

  @Override
  public void startup(Vertx vertx, Handler<AsyncResult<Void>> handler) {
    LOGGER.info("Startup of " + getClass().getSimpleName());
    try {
      if (datastore == null) {
        DataStoreSettings settings = MySqlDataStoreinit.createSettings();
        IDataStoreInit dsInit = settings.getDatastoreInit().newInstance();
        dsInit.initDataStore(vertx, settings, initResult -> {
          if (initResult.failed()) {
            LOGGER.error("could not start mysql client", initResult.cause());
            handler.handle(Future.failedFuture(new InitException(initResult.cause())));
          } else {
            datastore = (MySqlDataStore) initResult.result();
            handler.handle(Future.succeededFuture());
          }
        });
      } else {
        handler.handle(Future.succeededFuture());
      }
    } catch (Exception e) {
      LOGGER.error("", e);
      handler.handle(Future.failedFuture(e));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.test.IDatastoreContainer#getDataStore()
   */
  @Override
  public IDataStore getDataStore() {
    return datastore;
  }

  @Override
  public void shutdown(Handler<AsyncResult<Void>> handler) {
    LOGGER.info("shutdown performed");
    datastore.shutdown(result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
      }
      datastore = null;
      handler.handle(Future.succeededFuture());
    });
  }

  @Override
  public void dropTable(String tableName, Handler<AsyncResult<Void>> handler) {
    String command = "DROP TABLE IF EXISTS " + tableName;
    SqlUtil.execute(datastore, command, dr -> {
      if (dr.failed()) {
        LOGGER.error("error deleting table", dr.cause());
        handler.handle(Future.failedFuture(dr.cause()));
        return;
      }
      LOGGER.info("Deleted table " + tableName);
      handler.handle(Future.succeededFuture());
    });
  }

  @Override
  public String getExpectedTypehandlerName(Class<? extends AbstractTypeHandlerTest> testClass, String defaultName) {
    if (thMap.containsKey(testClass.getName()))
      return thMap.get(testClass.getName());
    return defaultName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.IDatastoreContainer#clearTable(java.lang.String,
   * io.vertx.core.Handler)
   */
  @Override
  public void clearTable(String tableName, Handler<AsyncResult<Void>> handler) {
    String command = "DELETE from " + tableName;
    SqlUtil.execute(datastore, command, dr -> {
      if (dr.failed()) {
        LOGGER.error("error deleting records", dr.cause());
        handler.handle(Future.failedFuture(dr.cause()));
        return;
      }
      LOGGER.info("Deleted records " + tableName);
      handler.handle(Future.succeededFuture());
    });
  }

}
