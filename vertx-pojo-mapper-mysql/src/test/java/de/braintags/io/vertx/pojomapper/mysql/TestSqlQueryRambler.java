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

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlExpression;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlQuery;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlQueryRambler;
import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.RamblerMapper;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

/**
 * testing of {@link SqlQueryRambler}
 * 
 * @author Michael Remme
 * 
 */

public class TestSqlQueryRambler extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestSqlQueryRambler.class);

  @Test
  public void test_1(TestContext context) {
    SqlQuery<RamblerMapper> query = (SqlQuery<RamblerMapper>) getDataStore(context).createQuery(RamblerMapper.class);
    query.field("name").is("name to find");
    executeRambler(context, query, 1);
  }

  @Test
  public void test_2(TestContext context) {
    SqlQuery<RamblerMapper> query = (SqlQuery<RamblerMapper>) getDataStore(context).createQuery(RamblerMapper.class);
    query.field("name").is("name to find").field("name").isNot("unknown");
    executeRambler(context, query, 2);
  }

  @Test
  public void test_3(TestContext context) {
    SqlQuery<RamblerMapper> query = (SqlQuery<RamblerMapper>) getDataStore(context).createQuery(RamblerMapper.class);
    query.or("name").is("name to find").field("name").isNot("unknown");
    executeRambler(context, query, 2);
  }

  @Test
  public void test_4(TestContext context) {
    SqlQuery<RamblerMapper> query = (SqlQuery<RamblerMapper>) getDataStore(context).createQuery(RamblerMapper.class);
    query.or("name").is("name to find").field("name").isNot("unknown").and("age").less(15);
    executeRambler(context, query, 3);
  }

  @Test
  public void test_5(TestContext context) {
    SqlQuery<RamblerMapper> query = (SqlQuery<RamblerMapper>) getDataStore(context).createQuery(RamblerMapper.class);
    query.or("name").is("name to find").field("name").isNot("unknown").and("age").in(4, 5, 7, 9);
    executeRambler(context, query, 6);
  }

  @Test
  public void test_6(TestContext context) {
    SqlQuery<RamblerMapper> query = (SqlQuery<RamblerMapper>) getDataStore(context).createQuery(RamblerMapper.class);
    query.orOpen("name").is("name to find").field("name").isNot("unknown");
    executeRambler(context, query, 2);
  }

  @Test
  public void test_7(TestContext context) {
    SqlQuery<RamblerMapper> query = (SqlQuery<RamblerMapper>) getDataStore(context).createQuery(RamblerMapper.class);
    query.orOpen("name").is("name to find").field("name").isNot("unknown").close().and("age").in(4, 5, 7, 9);
    executeRambler(context, query, 6);
  }

  private void executeRambler(TestContext context, SqlQuery<?> query, int expectedParameters) {
    Async async = context.async();
    SqlQueryRambler rambler = new SqlQueryRambler();
    query.executeQueryRambler(rambler, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
        async.complete();
      } else {
        LOGGER.info("SELECT STATEMENT: " + ((SqlExpression) rambler.getQueryExpression()).getSelectExpression());
        LOGGER.info("DELETE STATEMENT: " + ((SqlExpression) rambler.getQueryExpression()).getDeleteExpression());
        LOGGER.info(((SqlExpression) rambler.getQueryExpression()).getParameters());
        try {
          context.assertEquals(expectedParameters,
              ((SqlExpression) rambler.getQueryExpression()).getParameters().size(), "wrong number of parameters");
        } finally {
          async.complete();
        }
      }
    });
  }

}
// {"name":{"=":"looper"}}
// {"name":{"$eq":"Dublette"},"secondProperty":{"$eq":"erste"}}
// {"$and":[{"name":{"$eq":"Dublette"}},{"secondProperty":{"$eq":"erste"}}]}
// {"secondProperty":{"$nin":["erste","zweite"]}}
// {"$and":[{"name":{"$eq":"AndOr"}},{"$or":[{"secondProperty":{"$eq":"AndOr 1"}},{"secondProperty":{"$eq":"AndOr
// 2"}}]}]}
