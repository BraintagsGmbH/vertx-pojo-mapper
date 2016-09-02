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
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TestEncoder extends DatastoreBaseTest {
  private static final Logger log = LoggerFactory.getLogger(TestEncoder.class);

  /**
   * 
   */
  public TestEncoder() {
  }

  @Test
  public void simpleTest(TestContext context) {
    log.info("-->>test");
    context.fail("test mapper with annotation Encoded");
  }

}
