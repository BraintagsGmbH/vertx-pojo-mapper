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

import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.ResultSet;
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
  private static final Logger log = LoggerFactory.getLogger(TestSqlExpressions.class);

  // SELECT * FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='test' AND TABLE_NAME='SimpleMapper'

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
        log.info("found records: " + res.getNumRows());
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
        log.error("", createTableResult.cause());
        context.fail(createTableResult.cause());
        async.complete();
      } else {
        String insertExpression = "insert into timetable set myTime=?; ";
        JsonArray array = new JsonArray().add("2015-10-13 18:45:22");

        SqlUtil.updateWithParams((MySqlDataStore) getDataStore(context), insertExpression, array, ur -> {
          if (ur.failed()) {
            log.error("", ur.cause());
            async.complete();
          } else {
            UpdateResult res = ur.result();
            log.info(res.toJson());
            log.info(res.getKeys());

            String allRecords = "SELECT * from timetable";
            SqlUtil.query((MySqlDataStore) getDataStore(context), allRecords, idResult -> {
              if (idResult.failed()) {
                log.error("", idResult.cause());
                context.fail(idResult.cause());
                async.complete();
              } else {
                List<JsonObject> ids = idResult.result().getRows();
                for (JsonObject row : ids) {
                  log.info(row);
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
        log.error("", ur.cause());
        async.complete();
      } else {
        UpdateResult res = ur.result();
        log.info(res.toJson());
        log.info(res.getKeys());

        String lastInsertIdCmd = "SELECT LAST_INSERT_ID()";
        SqlUtil.query((MySqlDataStore) getDataStore(context), lastInsertIdCmd, idResult -> {
          if (idResult.failed()) {
            log.error("", ur.cause());
            context.fail(ur.cause());
            async.complete();
          } else {
            List<JsonObject> ids = idResult.result().getRows();
            for (JsonObject row : ids) {
              log.info(row);
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
        log.error("ERror searching", ur.cause());
        context.fail(ur.cause());
        async.complete();
      } else {
        ResultSet res = ur.result();
        log.info("found records: " + res.getNumRows());
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
        log.error("Error deleting", ur.cause());
        async.complete();
      } else {
        UpdateResult res = ur.result();
        log.info("deleted: " + res.getUpdated());
        async.complete();
      }
    });
  }

}
