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

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.datastoretest.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.RamblerMapper;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlQuery;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlQueryRambler;
import io.vertx.core.VertxOptions;

/**
 * testing of {@link SqlQueryRambler}
 * 
 * @author Michael Remme
 * 
 */

public class TestSqlQueryRambler extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestSqlQueryRambler.class);

  @Override
  protected VertxOptions getOptions() {
    VertxOptions options = new VertxOptions();
    options.setBlockedThreadCheckInterval(10000);
    options.setWarningExceptionTime(10000);
    return options;
  }

  @Test
  public void test_1() {
    SqlQuery<RamblerMapper> query = (SqlQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.field("name").is("name to find");
    executeRambler(query, 1);
  }

  @Test
  public void test_2() {
    SqlQuery<RamblerMapper> query = (SqlQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.field("name").is("name to find").field("name").isNot("unknown");
    executeRambler(query, 2);
  }

  @Test
  public void test_3() {
    SqlQuery<RamblerMapper> query = (SqlQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.or("name").is("name to find").field("name").isNot("unknown");
    executeRambler(query, 2);
  }

  @Test
  public void test_4() {
    SqlQuery<RamblerMapper> query = (SqlQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.or("name").is("name to find").field("name").isNot("unknown").and("age").less(15);
    executeRambler(query, 3);
  }

  @Test
  public void test_5() {
    SqlQuery<RamblerMapper> query = (SqlQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.or("name").is("name to find").field("name").isNot("unknown").and("age").in(4, 5, 7, 9);
    executeRambler(query, 6);
  }

  @Test
  public void test_6() {
    SqlQuery<RamblerMapper> query = (SqlQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.orOpen("name").is("name to find").field("name").isNot("unknown");
    executeRambler(query, 2);
  }

  @Test
  public void test_7() {
    SqlQuery<RamblerMapper> query = (SqlQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.orOpen("name").is("name to find").field("name").isNot("unknown").close().and("age").in(4, 5, 7, 9);
    executeRambler(query, 6);
  }

  private void executeRambler(SqlQuery<?> query, int expectedParameters) {
    CountDownLatch latch = new CountDownLatch(1);
    SqlQueryRambler rambler = new SqlQueryRambler();
    query.executeQueryRambler(rambler, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
        latch.countDown();
      } else {
        LOGGER.info("SELECT STATEMENT: " + rambler.getSqlStatement().getSelectExpression());
        LOGGER.info("DELETE STATEMENT: " + rambler.getSqlStatement().getDeleteExpression());
        LOGGER.info(rambler.getSqlStatement().getParameters());
        try {
          assertEquals("wrong number of parameters", expectedParameters,
              rambler.getSqlStatement().getParameters().size());
        } finally {
          latch.countDown();
        }
      }
    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      LOGGER.error("", e);
    } finally {
      testComplete();
    }
  }

}
// {"name":{"=":"looper"}}
// {"name":{"$eq":"Dublette"},"secondProperty":{"$eq":"erste"}}
// {"$and":[{"name":{"$eq":"Dublette"}},{"secondProperty":{"$eq":"erste"}}]}
// {"secondProperty":{"$nin":["erste","zweite"]}}
// {"$and":[{"name":{"$eq":"AndOr"}},{"$or":[{"secondProperty":{"$eq":"AndOr 1"}},{"secondProperty":{"$eq":"AndOr
// 2"}}]}]}
