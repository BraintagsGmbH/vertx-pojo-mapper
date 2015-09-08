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
package de.braintags.io.vertx.pojomapper.test;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import org.junit.Test;

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
  public void simpleTest2() {
    log.info("-->>test");
    assertNotNull(datastoreContainer);
    testComplete();
  }

}
