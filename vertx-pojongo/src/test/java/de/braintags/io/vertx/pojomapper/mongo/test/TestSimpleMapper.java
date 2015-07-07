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

import io.vertx.core.AsyncResult;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.IWrite.IWriteResult;
import de.braintags.io.vertx.pojomapper.mongo.test.mapper.SimpleMapper;

/**
 * Test the base actions by using a very simple mapper
 *
 * @author Michael Remme
 * 
 */

public class TestSimpleMapper extends MongoBaseTest {
  private static Logger logger = LoggerFactory.getLogger(TestSimpleMapper.class);

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

  /**
   * @throws java.lang.Exception
   */
  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    getMongoClient();
  }

  @Test
  public void testSimpleMapper() {
    SimpleMapper sm = new SimpleMapper();
    IWrite<SimpleMapper> write = getDataStore().createWrite(SimpleMapper.class);
    write.add(sm);
    write.save(result -> {
      assertTrue(resultFine(result));

    });

  }

  boolean resultFine(AsyncResult<IWriteResult> result) {
    if (result.failed()) {
      logger.error("", result.cause());
      return false;
    }
    return true;
  }
}
