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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.testdatastore.ResultContainer;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.MiniMapper;
import de.braintags.io.vertx.util.exception.ParameterRequiredException;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TestSqlExpressions extends DatastoreBaseTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(TestSqlExpressions.class);

  @Test
  public void executeNativeQuery(TestContext context) {
    MySqlDataStore ds = (MySqlDataStore) getDataStore(context);
    clearTable(context, MiniMapper.class);
    List<MiniMapper> write = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      write.add(new MiniMapper("native " + i));
    }
    saveRecords(context, write);
    IQuery<MiniMapper> query = ds.createQuery(MiniMapper.class);
    String qs = "select * from MiniMapper where name LIKE \"native%\"";
    query.setNativeCommand(qs);

    ResultContainer resultContainer = find(context, query, 10);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

  }

  @Test
  public void testWrongNativeFormat(TestContext context) {
    MySqlDataStore ds = (MySqlDataStore) getDataStore(context);
    IQuery<MiniMapper> query = ds.createQuery(MiniMapper.class);
    JsonObject qs = new JsonObject();
    query.setNativeCommand(qs);
    ResultContainer resultContainer = find(context, query, -1);
    if (resultContainer.assertionError == null)
      context.fail("Expected an exception here");
  }

  @Test
  public void testConnection(TestContext context) {
    Vertx vertx = Vertx.vertx();
    String host = System.getProperty("MySqlDataStoreContainer.host", null);
    if (host == null) {
      throw new ParameterRequiredException("you must set the property 'MySqlDataStoreContainer.host'");
    }
    String username = System.getProperty("MySqlDataStoreContainer.username", null);
    if (username == null) {
      throw new ParameterRequiredException("you must set the property 'MySqlDataStoreContainer.username'");
    }
    String password = System.getProperty("MySqlDataStoreContainer.password", null);
    if (password == null) {
      throw new ParameterRequiredException("you must set the property 'MySqlDataStoreContainer.password'");
    }

    JsonObject mySQLClientConfig = new JsonObject().put("host", host).put("username", username)
        .put("password", password).put("database", "test").put("port", 3306);
    AsyncSQLClient client = MySQLClient.createNonShared(vertx, mySQLClientConfig);
    client.getConnection(res -> {
      if (res.succeeded()) {
        SQLConnection connection = res.result();
        // Got a connection
        LOGGER.info("succeeded connection");
      } else {
        LOGGER.error("", res.cause());
        context.fail(res.cause());
      }
    });
  }

  @Test
  public void testQuerySchema(TestContext context) {
    Async async = context.async();
    String queryExpression = "SELECT * FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='test' AND TABLE_NAME='SimpleMapper'; ";
    SqlUtil.query((MySqlDataStore) getDataStore(context), queryExpression, ur -> {
      if (ur.failed()) {
        context.fail(ur.cause().toString());
        async.complete();
      } else {
        ResultSet res = ur.result();
        LOGGER.info("found records: " + res.getNumRows());
        async.complete();
      }
    });
  }

  @Test
  public void testTimeField(TestContext context) {
    String createTableString = "Create TABLE IF NOT EXISTS  timetable (id INT NOT NULL AUTO_INCREMENT, myTime TIMESTAMP, PRIMARY KEY(id))";
    Async async = context.async();
    SqlUtil.execute((MySqlDataStore) getDataStore(context), createTableString, createTableResult -> {
      if (createTableResult.failed()) {
        LOGGER.error("", createTableResult.cause());
        context.fail(createTableResult.cause());
        async.complete();
      } else {
        String insertExpression = "insert into timetable set myTime=?; ";
        JsonArray array = new JsonArray().add("2015-10-13 18:45:22");

        SqlUtil.updateWithParams((MySqlDataStore) getDataStore(context), insertExpression, array, ur -> {
          if (ur.failed()) {
            LOGGER.error("", ur.cause());
            async.complete();
          } else {
            UpdateResult res = ur.result();
            LOGGER.info(res.toJson());
            LOGGER.info(res.getKeys());

            String allRecords = "SELECT * from timetable";
            SqlUtil.query((MySqlDataStore) getDataStore(context), allRecords, idResult -> {
              if (idResult.failed()) {
                LOGGER.error("", idResult.cause());
                context.fail(idResult.cause());
                async.complete();
              } else {
                List<JsonObject> ids = idResult.result().getRows();
                for (JsonObject row : ids) {
                  LOGGER.info(row);
                }
                async.complete();
              }
            });
          }
        });
      }
    });
  }

  @Test
  public void simpleTest(TestContext context) {
    Async async = context.async();
    JsonArray array = new JsonArray().add("new name");
    String insertExpression = "insert into MiniMapper set name=?; ";

    SqlUtil.updateWithParams((MySqlDataStore) getDataStore(context), insertExpression, array, ur -> {
      if (ur.failed()) {
        LOGGER.error("", ur.cause());
        async.complete();
      } else {
        UpdateResult res = ur.result();
        LOGGER.info(res.toJson());
        LOGGER.info(res.getKeys());

        String lastInsertIdCmd = "SELECT LAST_INSERT_ID()";
        SqlUtil.query((MySqlDataStore) getDataStore(context), lastInsertIdCmd, idResult -> {
          if (idResult.failed()) {
            LOGGER.error("", ur.cause());
            context.fail(ur.cause());
            async.complete();
          } else {
            List<JsonObject> ids = idResult.result().getRows();
            for (JsonObject row : ids) {
              LOGGER.info(row);
            }
            async.complete();
          }
        });

      }
    });
  }

  @Test
  public void testQueryIN(TestContext context) {
    Async async = context.async();

    JsonArray array = new JsonArray().add("1").add("2").add("3");
    String insertExpression = "SELECT * from MiniMapper where id IN ( ?, ?, ?); ";

    SqlUtil.queryWithParams((MySqlDataStore) getDataStore(context), insertExpression, array, ur -> {
      if (ur.failed()) {
        LOGGER.error("ERror searching", ur.cause());
        context.fail(ur.cause());
        async.complete();
      } else {
        ResultSet res = ur.result();
        LOGGER.info("found records: " + res.getNumRows());
        async.complete();
      }
    });
  }

  @Test
  public void testDeleteIN(TestContext context) {
    Async async = context.async();
    JsonArray array = new JsonArray().add("1").add("2").add("3");
    String insertExpression = "Delete from MiniMapper where id IN ( ?, ?, ?); ";

    SqlUtil.updateWithParams((MySqlDataStore) getDataStore(context), insertExpression, array, ur -> {
      if (ur.failed()) {
        LOGGER.error("Error deleting", ur.cause());
        async.complete();
      } else {
        UpdateResult res = ur.result();
        LOGGER.info("deleted: " + res.getUpdated());
        async.complete();
      }
    });
  }

  @Test
  public void testGeoDirect(TestContext context) {
    final Async async1 = context.async();
    String insertExpression = "insert into GeoPointRecord set id=10, point = GeomFromText('POINT(18 -63)')";

    SqlUtil.update((MySqlDataStore) getDataStore(context), insertExpression, ur -> {
      if (ur.failed()) {
        LOGGER.error("Error deleting", ur.cause());
        async1.complete();
      } else {
        UpdateResult res = ur.result();
        LOGGER.info("deleted: " + res.getUpdated());
        async1.complete();
      }
    });
    async1.await();

    final Async async2 = context.async();

    String queryExpression = "SELECT id, AsText(point)  from GeoPointRecord; ";

    SqlUtil.query((MySqlDataStore) getDataStore(context), queryExpression, ur -> {
      if (ur.failed()) {
        LOGGER.error("ERror searching", ur.cause());
        context.fail(ur.cause());
        async2.complete();
      } else {
        ResultSet res = ur.result();
        LOGGER.info("found records: " + res.getNumRows());
        async2.complete();
      }
    });
    async2.await();

  }

  @Test
  public void testGeo(TestContext context) {
    Async async = context.async();
    // JsonArray array = new JsonArray().add(9).add(new SqlGeoPointTypeHandler.SqlFunction("GeomFromText", "POINT(18
    // -63)"));
    JsonArray array = new JsonArray().add(11).add("POINT(18 -63)");
    String insertExpression = "insert into GeoPointRecord set id=?, point = GeomFromText(?) ";

    // "insert into GeoPointRecord set id=?, point = GeomFromText(?) ";

    SqlUtil.updateWithParams((MySqlDataStore) getDataStore(context), insertExpression, array, ur -> {
      if (ur.failed()) {
        LOGGER.error("Error deleting", ur.cause());
        async.complete();
      } else {
        UpdateResult res = ur.result();
        LOGGER.info("deleted: " + res.getUpdated());
        async.complete();
      }
    });
  }

}
