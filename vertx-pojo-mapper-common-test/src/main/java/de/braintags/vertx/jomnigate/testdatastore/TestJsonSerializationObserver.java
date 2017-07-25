/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.testdatastore;

import org.junit.Test;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;

/**
 * Tests to improve correct mapping information for defined observers ( or by settings or annotation )
 * 
 * @author Michael Remme
 * 
 */
public class TestJsonSerializationObserver extends AbstractObserverTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestJsonSerializationObserver.class);

  @Test
  public void test_Serialization(TestContext context) {
    Vertx vertx = this.getDataStore(context).getVertx();
    LOGGER.info("EXISTS: " + vertx.fileSystem()
        .existsBlocking("/Users/mremme/workspace/vertx/vertx-pojo-mapper/vertx-pojo-mapper-common-test/tmp"));

  }

}
