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
package de.braintags.io.vertx.pojomapper.mongo.vertxunit;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.mongo.dataaccess.MongoQuery;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.MongoQueryRambler;
import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.RamblerMapper;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class TMongoQueryRambler extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TMongoQueryRambler.class);

  @Test
  public void test_1(TestContext context) {
    MongoQuery<RamblerMapper> query = (MongoQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.field("name").is("name to find");
    executeRambler(context, query, 1);
  }

  @Test
  public void test_2(TestContext context) {
    MongoQuery<RamblerMapper> query = (MongoQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.field("name").is("name to find").field("name").isNot("unknown");
    executeRambler(context, query, 2);
  }

  @Test
  public void test_3(TestContext context) {
    MongoQuery<RamblerMapper> query = (MongoQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.or("name").is("name to find").field("name").isNot("unknown");
    executeRambler(context, query, 2);
  }

  @Test
  public void test_4(TestContext context) {
    MongoQuery<RamblerMapper> query = (MongoQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.or("name").is("name to find").field("name").isNot("unknown").and("age").less(15);
    executeRambler(context, query, 3);
  }

  @Test
  public void test_5(TestContext context) {
    MongoQuery<RamblerMapper> query = (MongoQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.or("name").is("name to find").field("name").isNot("unknown").and("age").in(4, 5, 7, 9);
    executeRambler(context, query, 6);
  }

  @Test
  public void test_6(TestContext context) {
    MongoQuery<RamblerMapper> query = (MongoQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.orOpen("name").is("name to find").field("name").isNot("unknown");
    executeRambler(context, query, 2);
  }

  @Test
  public void test_7(TestContext context) {
    MongoQuery<RamblerMapper> query = (MongoQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.orOpen("name").is("name to find").field("name").isNot("unknown").close().and("age").in(4, 5, 7, 9);
    executeRambler(context, query, 6);
  }

  private void executeRambler(TestContext context, MongoQuery<?> query, int expectedParameters) {
    Async async = context.async();
    MongoQueryRambler rambler = new MongoQueryRambler();
    query.executeQueryRambler(rambler, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
      }
      async.complete();
    });
  }

}
