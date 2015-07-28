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

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.mongo.test.mapper.ObjectReferenceMapper;
import de.braintags.io.vertx.pojomapper.mongo.test.mapper.SimpleMapper;
import de.braintags.io.vertx.pojomapper.mongo.test.mapper.TypehandlerTestMapper;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

/**
 * Test the {@link ITypeHandler} used for mongo
 * 
 * @author Michael Remme
 * 
 */

public class TypeHandlerTest extends MongoBaseTest {
  private static Logger logger = LoggerFactory.getLogger(TypeHandlerTest.class);

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
  public void testSaveAndRead_TypehandlerTestMapper() {
    TypehandlerTestMapper sm = new TypehandlerTestMapper();
    ResultContainer resultContainer = saveRecord(sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<TypehandlerTestMapper> query = getDataStore().createQuery(TypehandlerTestMapper.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        result.cause().printStackTrace();
      } else {
        assertTrue(sm.equals(result.result()));
        logger.info("finished!");
      }
    });
  }

  @Test
  public void testSaveAndRead_ObjectReferenceMapper() {
    SimpleMapper sc = new SimpleMapper();

    ObjectReferenceMapper sm = new ObjectReferenceMapper();
    sm.simpleMapper = sc;

    ResultContainer resultContainer = saveRecord(sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<TypehandlerTestMapper> query = getDataStore().createQuery(TypehandlerTestMapper.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        result.cause().printStackTrace();
      } else {
        assertTrue(sm.equals(result.result()));
        logger.info("finished!");
      }
    });
  }

  /* ****************************************************
   * Helper Part
   */

}
