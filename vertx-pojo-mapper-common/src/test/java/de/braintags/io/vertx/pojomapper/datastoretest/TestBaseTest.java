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
package de.braintags.io.vertx.pojomapper.datastoretest;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TestBaseTest extends DatastoreBaseTest {
  private static final Logger log = LoggerFactory.getLogger(TestBaseTest.class);

  /**
   * 
   */
  public TestBaseTest() {
  }

  @Test
  public void simpleTest() {
    log.info("-->>test");
    assertNotNull(datastoreContainer);
    testComplete();
  }

  @Test
  public void testMetaData() {
    CountDownLatch latch = new CountDownLatch(1);
    assertNotNull(getDataStore().getMetaData());
    getDataStore().getMetaData().getVersion(result -> {
      if (result.failed()) {
        log.error("Error in testMetaData", result.cause());
        latch.countDown();
      } else {
        String version = result.result();
        assertNotNull(version);
        log.info(version);
        latch.countDown();
      }

    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      log.error("", e);
    } finally {
      testComplete();
    }
  }

}
