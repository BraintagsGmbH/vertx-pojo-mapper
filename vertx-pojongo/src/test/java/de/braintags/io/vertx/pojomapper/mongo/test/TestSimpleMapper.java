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

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.mongo.mapper.MongoMappedField;
import de.braintags.io.vertx.pojomapper.mongo.mapper.MongoMapper;
import de.braintags.io.vertx.pojomapper.mongo.mapper.MongoMapperFactory;
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
  public void test_01_MapperStructure() {
    assertTrue(getDataStore() instanceof MongoDataStore);
    assertTrue(getDataStore().getMapperFactory() instanceof MongoMapperFactory);
    IMapper mapper = getDataStore().getMapperFactory().getMapper(SimpleMapper.class);
    assertTrue(mapper instanceof MongoMapper);
    IField field = mapper.getField("name");
    assertTrue(field instanceof MongoMappedField);
  }

  @Test
  public void test_02_SimpleMapper() {
    SimpleMapper sm = new SimpleMapper();
    sm.name = "testName";
    sm.setSecondProperty("my second property");

    // ResultContainer rc = write(sm, true);
    // if (rc.assertionError != null)
    // throw rc.assertionError;
    //
    // await();

    IWrite<SimpleMapper> write = getDataStore().createWrite(SimpleMapper.class);
    write.add(sm);
    write.save(result -> {
      checkWriteResult(result);

      sm.id = (String) result.result().getId();
      sm.name = "testNameModified";
      sm.setSecondProperty("my modified property");
      IWrite<SimpleMapper> write2 = getDataStore().createWrite(SimpleMapper.class);
      write2.add(sm);
      write2.save(result2 -> {
        checkWriteResult(result2);

        IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
        query.execute(qResult -> {
          checkQueryResult(qResult);
        });

      });

    });

  }

  private void checkQueryResult(AsyncResult<IQueryResult<SimpleMapper>> qResult) {
    assertTrue(resultFine(qResult));
    assertNotNull(qResult.result());
    assertTrue(qResult.result().iterator().hasNext());
  }

  private void checkWriteResult(AsyncResult<IWriteResult> result) {
    assertTrue(resultFine(result));
    assertNotNull(result.result());
    assertNotNull(result.result().getStoreObject());
    assertNotNull(result.result().getId());
  }

  boolean resultFine(AsyncResult<?> result) {
    if (result.failed()) {
      logger.error("", result.cause());
      return false;
    }
    return true;
  }

  class ResultContainer {
    AssertionError assertionError;
    IWriteResult writeResult;
  }
}
