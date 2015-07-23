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

import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
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
  public void testSaveAndRead() {
    SimpleMapper sc = new SimpleMapper();

    // ResultContainer resultContainer = saveRecord(sc);
    // if (resultContainer.assertionError != null)
    // throw resultContainer.assertionError;
    // sc.id = (String) resultContainer.writeResult.getId();

    TypehandlerTestMapper sm = new TypehandlerTestMapper();
    sm.simpleMapper = sc;

    ResultContainer resultContainer = saveRecord(sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<TypehandlerTestMapper> query = getDataStore().createQuery(TypehandlerTestMapper.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    TypehandlerTestMapper foundSm = (TypehandlerTestMapper) resultContainer.queryResult.iterator().next();
    assertTrue(sm.equals(foundSm));
    logger.info("finished!");
  }

  /* ****************************************************
   * Helper Part
   */

  private ResultContainer find(IQuery<TypehandlerTestMapper> query, int expectedResult) {
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

  private void checkQueryResult(AsyncResult<IQueryResult<TypehandlerTestMapper>> qResult) {
    assertTrue(resultFine(qResult));
    IQueryResult<TypehandlerTestMapper> qr = qResult.result();
    assertNotNull(qr);
    assertTrue(qr.iterator().hasNext());
    TypehandlerTestMapper mapper = qr.iterator().next();
    assertNotNull(mapper);
  }

  private ResultContainer saveRecord(TypehandlerTestMapper sm) {
    ResultContainer resultContainer = new ResultContainer();
    CountDownLatch latch = new CountDownLatch(1);
    IWrite<TypehandlerTestMapper> write = getDataStore().createWrite(TypehandlerTestMapper.class);
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
