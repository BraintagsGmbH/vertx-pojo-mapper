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
package de.braintags.io.vertx.pojomapper.testdatastore;

import org.junit.Test;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

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
  public void simpleTest(TestContext context) {
    log.info("-->>test");
    context.assertNotNull(TestHelper.getDatastoreContainer(context));
  }

  @Test
  public void testMetaData(TestContext context) {
    Async async = context.async();
    context.assertNotNull(getDataStore(context));
    context.assertNotNull(getDataStore(context).getMetaData());
    getDataStore(context).getMetaData().getVersion(result -> {
      if (result.failed()) {
        log.error("Error in testMetaData", result.cause());
        async.complete();
        context.fail(result.cause());
      } else {
        String version = result.result();
        context.assertNotNull(version);
        log.info("Version is: " + version);
        async.complete();
      }
    });
  }

}
