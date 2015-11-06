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
package de.braintags.io.vertx.pojomapper.mongo.testNew;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.datastoretest.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.RamblerMapper;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.MongoQuery;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.MongoQueryRambler;
import io.vertx.core.VertxOptions;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class TestMongoQueryRambler extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestMongoQueryRambler.class);

  @Override
  protected VertxOptions getOptions() {
    VertxOptions options = new VertxOptions();
    options.setBlockedThreadCheckInterval(10000);
    options.setWarningExceptionTime(10000);
    return options;
  }

  @Test
  public void test_1() {
    MongoQuery<RamblerMapper> query = (MongoQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.field("name").is("name to find");
    executeRambler(query, 1);
  }

  @Test
  public void test_2() {
    MongoQuery<RamblerMapper> query = (MongoQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.field("name").is("name to find").field("name").isNot("unknown");
    executeRambler(query, 2);
  }

  @Test
  public void test_3() {
    MongoQuery<RamblerMapper> query = (MongoQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.or("name").is("name to find").field("name").isNot("unknown");
    executeRambler(query, 2);
  }

  @Test
  public void test_4() {
    MongoQuery<RamblerMapper> query = (MongoQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.or("name").is("name to find").field("name").isNot("unknown").and("age").less(15);
    executeRambler(query, 3);
  }

  @Test
  public void test_5() {
    MongoQuery<RamblerMapper> query = (MongoQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.or("name").is("name to find").field("name").isNot("unknown").and("age").in(4, 5, 7, 9);
    executeRambler(query, 6);
  }

  @Test
  public void test_6() {
    MongoQuery<RamblerMapper> query = (MongoQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.orOpen("name").is("name to find").field("name").isNot("unknown");
    executeRambler(query, 2);
  }

  @Test
  public void test_7() {
    MongoQuery<RamblerMapper> query = (MongoQuery<RamblerMapper>) getDataStore().createQuery(RamblerMapper.class);
    query.orOpen("name").is("name to find").field("name").isNot("unknown").close().and("age").in(4, 5, 7, 9);
    executeRambler(query, 6);
  }

  private void executeRambler(MongoQuery<?> query, int expectedParameters) {
    CountDownLatch latch = new CountDownLatch(1);
    MongoQueryRambler rambler = new MongoQueryRambler();
    query.executeQueryRambler(rambler, result -> {
      if (result.failed()) {
        LOGGER.error("", result.cause());
        latch.countDown();
      } else {
        latch.countDown();
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
