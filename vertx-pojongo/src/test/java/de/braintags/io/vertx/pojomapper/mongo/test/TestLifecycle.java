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
package de.braintags.io.vertx.pojomapper.mongo.test;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.mongo.test.mapper.LifecycleMapper;

/**
 * Test the base actions by using a very simple mapper
 *
 * @author Michael Remme
 * 
 */

public class TestLifecycle extends MongoBaseTest {
  private static Logger logger = LoggerFactory.getLogger(TestLifecycle.class);

  @BeforeClass
  public static void beforeClass() throws Exception {
    System.setProperty("connection_string", "mongodb://localhost:27017");
    System.setProperty("db_name", "PojongoTestDatabase");
    MongoBaseTest.startMongo();
  }

  @AfterClass
  public static void afterClass() {
    MongoBaseTest.stopMongo();
  }

  @Test
  public void testLifecycle() {
    LifecycleMapper lcm = new LifecycleMapper();
    lcm.name = "Lifecycle mapper";
    saveRecord(lcm);

    assertEquals("before save", lcm.beforeSaveProperty);
    assertEquals("after save", lcm.afterSaveProperty);

    IQuery<LifecycleMapper> query = getDataStore().createQuery(LifecycleMapper.class);
    query.field("name").is(lcm.name);
    ResultContainer cnt = find(query, 1);
    CountDownLatch latch = new CountDownLatch(1);

    cnt.queryResult.iterator().next(itResult -> {
      if (itResult.failed()) {
        logger.error("", itResult.cause());
        fail(itResult.cause().toString());
        latch.countDown();
      } else {
        LifecycleMapper mapper = (LifecycleMapper) itResult.result();
        assertEquals("after load", mapper.afterLoadProperty);
        IDelete<LifecycleMapper> delete = getDataStore().createDelete(LifecycleMapper.class);
        delete.add(mapper);
        delete.delete(deleteResult -> {
          if (deleteResult.failed()) {
            logger.error("", deleteResult.cause());
            fail(deleteResult.cause().toString());
            latch.countDown();
          } else {
            try {
              assertEquals("before delete", mapper.beforeDeleteProperty);
              assertEquals("after delete", mapper.afterDeleteProperty);
            } finally {
              latch.countDown();
            }
          }
        });

      }

    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }
}
