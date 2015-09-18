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

import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TestBaseTest extends MongoBaseTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(TestBaseTest.class);

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

  @Override
  protected VertxOptions getOptions() {
    VertxOptions options = new VertxOptions();
    options.setBlockedThreadCheckInterval(10000);
    options.setWarningExceptionTime(10000);
    return options;
  }

  /**
   * 
   */
  public TestBaseTest() {
  }

  @Test
  public void simpleTest() {
    LOGGER.info("-->>test");
    testComplete();
  }

  @Test
  public void simpleTest2() {
    LOGGER.info("-->>test");
    testComplete();
  }

  @Test
  public void testMetaData() {
    CountDownLatch latch = new CountDownLatch(1);
    assertNotNull(getDataStore().getMetaData());
    getDataStore().getMetaData().getVersion(result -> {
      if (result.failed()) {
        LOGGER.error("Error in testMetaData", result.cause());
        latch.countDown();
      } else {
        String version = result.result();
        assertNotNull(version);
        LOGGER.info(version);
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
