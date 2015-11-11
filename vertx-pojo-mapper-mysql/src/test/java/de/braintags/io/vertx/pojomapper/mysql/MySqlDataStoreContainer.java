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
import de.braintags.io.vertx.pojomapper.datastoretest.IDatastoreContainer;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.AbstractTypeHandlerTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.ArrayTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.BooleanTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.CalendarTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.CollectionTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.DateTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.EmbeddedArrayTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.EmbeddedListTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.EmbeddedMapTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.EmbeddedSingleTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.EmbeddedSingleTest_Null;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.JsonTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.MapTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.PropertiesTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.ReferencedArrayTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.ReferencedListTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.ReferencedMapTest;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.ReferencedSingleTest;
import de.braintags.io.vertx.pojomapper.exception.ParameterRequiredException;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.BooleanTypeHandler;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.JsonTypeHandler;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlArrayTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlArrayTypeHandlerReferenced;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlArrayTypehandler;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlCalendarTypehandler;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlCollectionTypeHandler;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlCollectionTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlCollectionTypeHandlerReferenced;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlDateTypeHandler;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlMapTypeHandler;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlMapTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlMapTypeHandlerReferenced;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlObjectTypehandlerEmbedded;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlObjectTypehandlerReferenced;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;

/**
 * 
 * @author Michael Remme
 * 
 */

public class MySqlDataStoreContainer implements IDatastoreContainer {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(MySqlDataStoreContainer.class);

  private MySqlDataStore datastore;
  private AsyncSQLClient mySQLClient;
  private Map<String, String> thMap = new HashMap<String, String>();

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
  }

  @Override
  public void startup(Vertx vertx, Handler<AsyncResult<Void>> handler) {
    try {
      String username = System.getProperty("MySqlDataStoreContainer.username", null);
      if (username == null) {
        throw new ParameterRequiredException("you must set the property 'MySqlDataStoreContainer.username'");
      }
      String password = System.getProperty("MySqlDataStoreContainer.password", null);
      if (password == null) {
        throw new ParameterRequiredException("you must set the property 'MySqlDataStoreContainer.password'");
      }

      String database = "test";
      JsonObject mySQLClientConfig = new JsonObject().put("host", "localhost").put("username", username)
          .put("password", password).put("database", database).put("port", 3306).put("initial_pool_size", 10);
      mySQLClient = MySQLClient.createShared(vertx, mySQLClientConfig);
      datastore = new MySqlDataStore(mySQLClient, database);
      handler.handle(Future.succeededFuture());
    } catch (Exception e) {
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
    mySQLClient.close(handler);
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

}
