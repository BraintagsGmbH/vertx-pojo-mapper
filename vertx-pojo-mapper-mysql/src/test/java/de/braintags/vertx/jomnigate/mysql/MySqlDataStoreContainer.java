/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.vertx.jomnigate.mysql;

import java.util.HashMap;
import java.util.Map;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.init.IDataStoreInit;
import de.braintags.vertx.jomnigate.mysql.init.MySqlDataStoreinit;
import de.braintags.vertx.jomnigate.mysql.typehandler.BooleanTypeHandler;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlArrayTypeHandlerEmbedded;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlArrayTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlArrayTypehandler;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlCalendarTypehandler;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlCollectionTypeHandler;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlCollectionTypeHandlerEmbedded;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlCollectionTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlDateTypeHandler;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlGeoPointTypeHandler;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlMapTypeHandler;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlMapTypeHandlerEmbedded;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlMapTypeHandlerReferenced;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlObjectTypehandlerEmbedded;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlObjectTypehandlerReferenced;
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
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.JsonTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.MapTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.PropertiesTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.ReferencedArrayTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.ReferencedListTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.ReferencedMapTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.ReferencedSingleTest;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.json.geo.GeoPointTest;
import de.braintags.vertx.jomnigate.typehandler.stringbased.handlers.JsonTypeHandler;
import de.braintags.vertx.util.exception.InitException;
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
        DataStoreSettings settings = createSettings();
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

  @Override
  public DataStoreSettings createSettings() {
    return MySqlDataStoreinit.createSettings();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.test.IDatastoreContainer#getDataStore()
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
   * @see de.braintags.vertx.jomnigate.testdatastore.IDatastoreContainer#clearTable(java.lang.String,
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
