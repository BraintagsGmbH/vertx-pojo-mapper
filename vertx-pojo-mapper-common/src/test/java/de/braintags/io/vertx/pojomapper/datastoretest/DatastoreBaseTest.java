/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.datastoretest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.exception.ParameterRequiredException;
import de.braintags.io.vertx.util.ErrorObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.test.core.VertxTestBase;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class DatastoreBaseTest extends VertxTestBase {
  private static final Logger logger = LoggerFactory.getLogger(DatastoreBaseTest.class);
  public static IDatastoreContainer datastoreContainer;

  /**
   * 
   */
  public DatastoreBaseTest() {
  }

  public IDataStore getDataStore() {
    return datastoreContainer.getDataStore();
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.test.core.VertxTestBase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    logger.info("setup");
    super.setUp();
    String property = System.getProperty(IDatastoreContainer.PROPERTY);
    if (property == null) {
      throw new ParameterRequiredException("Need the parameter " + IDatastoreContainer.PROPERTY
          + ". Start the test with -DIDatastoreContainer=de.braintags.io.vertx.pojomapper.mysql.MySqlDataStoreContainer for instance");
    }
    datastoreContainer = (IDatastoreContainer) Class.forName(property).newInstance();
    CountDownLatch latch = new CountDownLatch(1);
    ErrorObject<Void> err = new ErrorObject<Void>(null);
    datastoreContainer.startup(vertx, result -> {
      if (result.failed()) {
        err.setThrowable(result.cause());
      }
      latch.countDown();
    });

    latch.await(5, TimeUnit.SECONDS);
    if (err.isError())
      throw err.getRuntimeException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see io.vertx.test.core.VertxTestBase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    logger.info("teardown");
    super.tearDown();

    CountDownLatch latch = new CountDownLatch(1);
    ErrorObject<Void> err = new ErrorObject<Void>(null);
    datastoreContainer.shutdown(result -> {
      if (result.failed()) {
        err.setThrowable(result.cause());
      }
      latch.countDown();
    });

    latch.await(5, TimeUnit.SECONDS);
    if (err.isError())
      throw err.getRuntimeException();

  }

  public ResultContainer saveRecords(List<?> records) {
    ResultContainer resultContainer = new ResultContainer();
    CountDownLatch latch = new CountDownLatch(1);
    IWrite<Object> write = (IWrite<Object>) getDataStore().createWrite(records.get(0).getClass());
    for (Object record : records) {
      write.add(record);
    }
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

  public ResultContainer saveRecord(Object sm) {
    ResultContainer resultContainer = new ResultContainer();
    CountDownLatch latch = new CountDownLatch(1);
    IWrite<Object> write = (IWrite<Object>) getDataStore().createWrite(sm.getClass());
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

  public void checkWriteResult(AsyncResult<IWriteResult> result) {
    resultFine(result);
    assertNotNull(result.result());
    IWriteEntry entry = result.result().iterator().next();
    assertNotNull(entry);
    assertNotNull(entry.getStoreObject());
    assertNotNull(entry.getId());
  }

  public void resultFine(AsyncResult<?> result) {
    if (result.failed()) {
      logger.error("", result.cause());
      throw new AssertionError("result failed", result.cause());
    }
  }

  /**
   * Executes a query and checks for the expected result
   * 
   * @param query
   *          the query to be executed
   * @param expectedResult
   *          the expected number of records
   * @return ResultContainer with certain informations
   */
  public ResultContainer find(IQuery<?> query, int expectedResult) {
    ResultContainer resultContainer = new ResultContainer();
    CountDownLatch latch = new CountDownLatch(1);
    query.execute(result -> {
      try {
        resultContainer.queryResult = result.result();
        checkQueryResult(result, expectedResult);

        logger.info(resultContainer.queryResult.getOriginalQuery());
        assertEquals(expectedResult, resultContainer.queryResult.size());

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

  /**
   * Executes a query and checks for the expected result
   * 
   * @param query
   *          the query to be executed
   * @param expectedResult
   *          the expected number of records
   * @return ResultContainer with certain informations
   */
  public ResultContainer findCount(IQuery<?> query, int expectedResult) {
    ResultContainer resultContainer = new ResultContainer();
    CountDownLatch latch = new CountDownLatch(1);
    query.executeCount(result -> {
      try {
        resultContainer.queryResultCount = result.result();
        checkQueryResultCount(result, expectedResult);
        logger.info(resultContainer.queryResultCount.getOriginalQuery());
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

  /**
   * Performs the delete action and processes the checkQuery to improve the correct result
   * 
   * @param delete
   *          the {@link IDelete} to be executed
   * @param checkQuery
   *          the query to improve the correct result
   * @param expectedResult
   *          the expected result of checkQuery
   * @return {@link ResultContainer} with deleteResult and queryResult
   */
  public ResultContainer delete(IDelete<?> delete, IQuery<?> checkQuery, int expectedResult) {
    ResultContainer resultContainer = new ResultContainer();
    CountDownLatch latch = new CountDownLatch(1);
    delete.delete(result -> {
      try {
        resultContainer.deleteResult = result.result();
        checkDeleteResult(result);

        ResultContainer queryResult = find(checkQuery, expectedResult);
        resultContainer.assertionError = queryResult.assertionError;
        resultContainer.queryResult = queryResult.queryResult;
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

  public void checkDeleteResult(AsyncResult<? extends IDeleteResult> dResult) {
    resultFine(dResult);
    IDeleteResult dr = dResult.result();
    assertNotNull(dr);
    assertNotNull(dr.getOriginalCommand());
    logger.info(dr.getOriginalCommand());
  }

  public void checkQueryResultCount(AsyncResult<? extends IQueryCountResult> qResult, int expectedResult) {
    resultFine(qResult);
    IQueryCountResult qr = qResult.result();
    assertNotNull(qr);
    assertEquals(expectedResult, qr.getCount());
  }

  public void checkQueryResult(AsyncResult<? extends IQueryResult<?>> qResult, int expectedResult) {
    CountDownLatch latch = new CountDownLatch(1);
    resultFine(qResult);
    IQueryResult<?> qr = qResult.result();
    assertNotNull(qr);
    if (expectedResult == 0) {
      try {
        assertFalse(qr.iterator().hasNext());
      } finally {
        latch.countDown();
      }
    } else {
      assertTrue(qr.iterator().hasNext());
      qr.iterator().next(result -> {
        try {
          if (result.failed()) {
            result.cause().printStackTrace();
            throw result.cause() instanceof RuntimeException ? (RuntimeException) result.cause()
                : new RuntimeException(result.cause());
          } else {
            assertNotNull(result.result());
          }
        } finally {
          latch.countDown();
        }
      });
    }

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
