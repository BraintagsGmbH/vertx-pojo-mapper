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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
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

  @Test
  public void testMapperStructure() {
    assertTrue(getDataStore() instanceof MongoDataStore);
    assertTrue(getDataStore().getMapperFactory() instanceof MongoMapperFactory);
    IMapper mapper = getDataStore().getMapperFactory().getMapper(SimpleMapper.class);
    assertTrue(mapper instanceof MongoMapper);
    IField field = mapper.getField("name");
    assertTrue(field instanceof MongoMappedField);
  }

  @Test
  public void testSimpleMapper() {
    SimpleMapper sm = new SimpleMapper();
    sm.name = "testName";
    sm.setSecondProperty("my second property");
    ResultContainer resultContainer = saveRecord(sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
    IWriteEntry we = resultContainer.writeResult.iterator().next();
    sm.id = (String) we.getId();
    sm.name = "testNameModified";
    sm.setSecondProperty("my modified property");
    resultContainer = saveRecord(sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        logger.error("", result.cause());
        fail(result.cause().toString());
      } else {
        assertTrue(sm.equals(result.result()));

        // search inside name field
        query.field("name").is("testNameModified");
        ResultContainer resultContainer2 = find(query, 1);
        if (resultContainer2.assertionError != null)
          throw resultContainer2.assertionError;

        resultContainer2.queryResult.iterator().next(res2 -> {
          if (res2.failed()) {
            logger.error("", result.cause());
            fail(result.cause().toString());
          } else {
            assertTrue(sm.equals(result.result()));
          }

        });

      }
    });

  }

  @Test
  public void testSimpleOr() {
    createDemoRecords();

    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    query.field("name").is("Dublette");
    ResultContainer resultContainer = find(query, 2);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    query = getDataStore().createQuery(SimpleMapper.class);
    query.or("secondProperty").is("erste").field("secondProperty").is("zweite");
    resultContainer = find(query, 2);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  @Test
  public void testSimpleAnd() {
    createDemoRecords();

    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    query.and("name").is("Dublette").field("secondProperty").is("erste");
    ResultContainer resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  @Test
  public void testQueryMultipleFields() {
    createDemoRecords();
    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    query.field("name").is("Dublette");
    ResultContainer resultContainer = find(query, 2);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    query.field("secondProperty").is("erste");
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search: Name = "AndOr" AND ( secondProperty="AndOr 1" OR secondProperty="AndOr 2" )
   */
  @Test
  public void testAndOr() {
    createDemoRecords();
    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    query.and("name").is("AndOr").or("secondProperty").is("AndOr 1").field("secondProperty").is("AndOr 2");

    ResultContainer resultContainer = find(query, 2);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search:
   */
  @Test
  public void testIn() {
    createDemoRecords();
    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    List<String> it = Arrays.asList("Dublette", "AndOr");
    query.field("name").in(it);

    ResultContainer resultContainer = find(query, 5);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search:
   */
  @Test
  public void testNotIn() {
    createDemoRecords();
    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    List<String> it = Arrays.asList("erste", "zweite");
    query.field("secondProperty").notIn(it);

    ResultContainer resultContainer = find(query, 3);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search:
   */
  @Test
  public void testIsNot() {
    createDemoRecords();
    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    query.field("name").isNot("Dublette");

    ResultContainer resultContainer = find(query, 3);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /* ****************************************************
   * Helper Part
   */

  private void createDemoRecords() {
    SimpleMapper sm = new SimpleMapper();
    sm.name = "Dublette";
    sm.setSecondProperty("erste");
    sm.intValue = 10;
    ResultContainer resultContainer = saveRecord(sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    sm = new SimpleMapper();
    sm.name = "Dublette";
    sm.setSecondProperty("zweite");
    sm.intValue = 11;
    resultContainer = saveRecord(sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    for (int i = 0; i < 3; i++) {
      sm = new SimpleMapper();
      sm.name = "AndOr";
      sm.setSecondProperty("AndOr " + i);
      sm.intValue = i + 1;
      resultContainer = saveRecord(sm);
      if (resultContainer.assertionError != null)
        throw resultContainer.assertionError;
    }

  }

  private ResultContainer find(IQuery<SimpleMapper> query, int expectedResult) {
    ResultContainer resultContainer = new ResultContainer();
    CountDownLatch latch = new CountDownLatch(1);
    query.execute(result -> {
      try {
        resultContainer.queryResult = result.result();
        checkQueryResult(result);

        assertEquals(expectedResult, resultContainer.queryResult.size());
        logger.info(resultContainer.queryResult.getOriginalQuery());

      } catch (AssertionError e) {
        resultContainer.assertionError = e;
      } catch (Throwable e) {
        resultContainer.assertionError = new AssertionError(e);
      } finally {
        latch.countDown();
      }
    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return resultContainer;
  }

  private void checkQueryResult(AsyncResult<IQueryResult<SimpleMapper>> qResult) {
    CountDownLatch latch = new CountDownLatch(1);
    assertTrue(resultFine(qResult));
    IQueryResult<SimpleMapper> qr = qResult.result();
    assertNotNull(qr);
    assertTrue(qr.iterator().hasNext());
    qr.iterator().next(result -> {
      try {
        if (result.failed()) {
          result.cause().printStackTrace();
        } else {
          assertNotNull(result.result());
        }
      } finally {
        latch.countDown();
      }
    });
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private ResultContainer saveRecord(SimpleMapper sm) {
    ResultContainer resultContainer = new ResultContainer();
    CountDownLatch latch = new CountDownLatch(1);
    IWrite<SimpleMapper> write = getDataStore().createWrite(SimpleMapper.class);
    write.add(sm);
    write.save(result -> {
      try {
        resultContainer.writeResult = result.result();
        checkWriteResult(result);
      } catch (AssertionError e) {
        resultContainer.assertionError = e;
      } catch (Throwable e) {
        resultContainer.assertionError = new AssertionError(e);
      } finally {
        latch.countDown();
      }
    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return resultContainer;
  }

}
