/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mongo.test;

import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TestBaseTest extends MongoBaseTest {
  private static final Logger log = LoggerFactory.getLogger(TestBaseTest.class);

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

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.test.core.VertxTestBase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    log.info("-->> setup");
    super.setUp();
    getMongoClient();
    dropCollections();
  }

  @Override
  protected VertxOptions getOptions() {
    VertxOptions options = new VertxOptions();
    options.setBlockedThreadCheckPeriod(10000);
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
    log.info("-->>test");
  }

  @Test
  public void simpleTest2() {
    log.info("-->>test");
  }

}
