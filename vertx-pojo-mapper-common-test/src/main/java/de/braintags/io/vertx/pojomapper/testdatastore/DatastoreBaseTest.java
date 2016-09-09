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

package de.braintags.io.vertx.pojomapper.testdatastore;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.runner.RunWith;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.util.QueryHelper;
import de.braintags.io.vertx.util.ErrorObject;
import de.braintags.io.vertx.util.ExceptionUtil;
import de.braintags.io.vertx.util.IteratorAsync;
import de.braintags.io.vertx.util.ResultObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

@RunWith(VertxUnitRunner.class)
public abstract class DatastoreBaseTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(DatastoreBaseTest.class);

  @SuppressWarnings("unused")
  private static final String COLLECTION = "MySequenceCollection";

  /**
   * Set the datastore from external to use the helper methods
   */
  public static IDataStore EXTERNAL_DATASTORE;

  @Rule
  public Timeout rule = Timeout.seconds(Integer.parseInt(System.getProperty("testTimeout", "40")));

  public static IDataStore getDataStore(TestContext context) {
    return EXTERNAL_DATASTORE == null ? TestHelper.getDatastoreContainer(context).getDataStore() : EXTERNAL_DATASTORE;
  }

  public static ResultContainer saveRecords(TestContext context, List<?> records) {
    return saveRecords(context, records, 0);
  }

  /**
   * save the list of records
   * 
   * @param records
   *          the records to be saved
   * @param waittime
   *          the time to wait for saving
   * @return the result
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static ResultContainer saveRecords(TestContext context, List<?> records, int waittime) {
    Async async = context.async();
    ResultContainer resultContainer = new ResultContainer();
    ErrorObject err = new ErrorObject<>(null);
    IWrite<Object> write = (IWrite<Object>) getDataStore(context).createWrite(records.get(0).getClass());
    for (Object record : records) {
      write.add(record);
    }
    write.save(result -> {
      if (result.failed()) {
        err.setThrowable(result.cause());
        async.complete();
      } else {
        try {
          resultContainer.writeResult = result.result();
          checkWriteResult(context, result, records.size());
        } catch (AssertionError e) {
          LOGGER.error("", e); // logging in case the ResultContainer is not handled in caller
          err.setThrowable(e);
        } catch (Throwable e) {
          LOGGER.error("", e);// logging in case the ResultContainer is not handled in caller
          err.setThrowable(e);
        } finally {
          async.complete();
        }
      }
    });

    if (waittime == 0) {
      async.await();
    } else {
      async.await(waittime);
    }
    if (err.isError()) {
      LOGGER.error("", err.getThrowable());
      throw new AssertionError(err.getThrowable());
    }
    return resultContainer;
  }

  public static ResultContainer saveRecord(TestContext context, Object sm) {
    return saveRecords(context, Arrays.asList(sm));
  }

  public static void checkWriteResult(TestContext context, AsyncResult<IWriteResult> result,
      int expectedNumberOfRecords) {
    resultFine(result);
    context.assertNotNull(result.result());
    IWriteEntry entry = result.result().iterator().next();
    context.assertNotNull(entry);
    context.assertNotNull(entry.getStoreObject());
    context.assertNotNull(entry.getId(), "the id of an IWriteEntry must be defined");
    if (expectedNumberOfRecords >= 0) {
      context.assertEquals(expectedNumberOfRecords, result.result().size());
    }
  }

  public static void resultFine(AsyncResult<?> result) {
    if (result.failed()) {
      LOGGER.error("", result.cause());
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
  @SuppressWarnings("rawtypes")
  public static ResultContainer find(TestContext context, IQuery<?> query, int expectedResult) {
    Async async = context.async();
    ResultContainer resultContainer = new ResultContainer();
    ErrorObject err = new ErrorObject<>(null);
    query.execute(result -> {
      try {
        resultFine(result);
        resultContainer.queryResult = result.result();
        LOGGER.info("performed find with: " + resultContainer.queryResult.getOriginalQuery());
      } catch (Throwable e) {
        err.setThrowable(e);
      } finally {
        async.complete();
      }
    });

    async.await();
    if (err.isError()) {
      throw new AssertionError(err.getThrowable());
    }
    checkQueryResult(context, resultContainer.queryResult, expectedResult);
    return resultContainer;
  }

  /**
   * Executes a query for a record with the given ID
   * 
   * @param context
   * @param mapperClass
   * @param id
   * @return
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static Object findRecordByID(TestContext context, Class mapperClass, String id) {
    Async async = context.async();
    ResultObject res = new ResultObject(null);
    QueryHelper.findRecordById(getDataStore(context), mapperClass, id, result -> {
      if (result.failed()) {
        res.setThrowable(result.cause());
        async.complete();
      } else {
        res.setResult(result.result());
        async.complete();
      }
    });
    async.await();
    if (res.isError()) {
      throw res.getRuntimeException();
    } else {
      return res.getResult();
    }
  }

  /**
   * Executes a query and returns directly the first record
   * 
   * @param context
   *          the context to be used
   * @param query
   *          the query to be executed
   * @return Object the first instance found or null if none
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static Object findFirst(TestContext context, IQuery<?> query) {
    Async async = context.async();
    ResultObject res = new ResultObject(null);
    QueryHelper.executeToFirstRecord(query, result -> {
      if (result.failed()) {
        res.setThrowable(result.cause());
        async.complete();
      } else {
        res.setResult(result.result());
        async.complete();
      }
    });

    async.await();
    if (res.isError()) {
      throw res.getRuntimeException();
    } else {
      return res.getResult();
    }
  }

  /**
   * Executes a query and returns all found records
   * 
   * @param context
   *          the context to be used
   * @param query
   *          the query to be executed
   * @return teh list of records
   */
  public static List<?> findAll(TestContext context, IQuery<?> query) {
    Async async = context.async();
    ResultObject<List<?>> res = new ResultObject<>(null);
    QueryHelper.executeToList(query, result -> {
      if (result.failed()) {
        res.setThrowable(result.cause());
        async.complete();
      } else {
        res.setResult(result.result());
        async.complete();
      }
    });

    async.await();
    if (res.isError()) {
      throw res.getRuntimeException();
    } else {
      return res.getResult();
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
  @SuppressWarnings("rawtypes")
  public static ResultContainer findCount(TestContext context, IQuery<?> query, int expectedResult) {
    Async async = context.async();
    ResultContainer resultContainer = new ResultContainer();
    ErrorObject err = new ErrorObject<>(null);
    query.executeCount(result -> {
      try {
        resultContainer.queryResultCount = result.result();
        LOGGER.info(
            resultContainer.queryResultCount.getOriginalQuery() + ": " + resultContainer.queryResultCount.getCount());
        checkQueryResultCount(context, result, expectedResult);
      } catch (Throwable e) {
        err.setThrowable(e);
      } finally {
        async.complete();
      }
    });

    async.await();
    if (err.isError()) {
      throw new AssertionError(err.getThrowable());
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
  @SuppressWarnings("rawtypes")
  public static ResultContainer delete(TestContext context, IDelete<?> delete, IQuery<?> checkQuery,
      int expectedResult) {
    Async async = context.async();
    ResultContainer resultContainer = new ResultContainer();
    ErrorObject err = new ErrorObject<>(null);
    delete.delete(result -> {
      try {
        resultContainer.deleteResult = result.result();
        checkDeleteResult(context, result);
      } catch (Throwable e) {
        err.setThrowable(e);
      } finally {
        async.complete();
      }
    });

    async.await();
    if (err.isError()) {
      throw new AssertionError(err.getThrowable());
    }

    // Now perform the query check
    if (checkQuery != null) {
      ResultContainer queryResult = find(context, checkQuery, expectedResult);
      resultContainer.queryResult = queryResult.queryResult;
      return resultContainer;
    }
    return null;
  }

  public static void checkDeleteResult(TestContext context, AsyncResult<? extends IDeleteResult> dResult) {
    resultFine(dResult);
    IDeleteResult dr = dResult.result();
    context.assertNotNull(dr);
    context.assertNotNull(dr.getOriginalCommand());
    LOGGER.info(dr.getOriginalCommand());
  }

  public static void checkQueryResultCount(TestContext context, AsyncResult<? extends IQueryCountResult> qResult,
      int expectedResult) {
    resultFine(qResult);
    IQueryCountResult qr = qResult.result();
    context.assertNotNull(qr);
    if (expectedResult >= 0) {
      context.assertEquals(new Long(expectedResult), new Long(qr.getCount()));
    }
  }

  @SuppressWarnings({ "rawtypes" })
  public static void checkQueryResult(TestContext context, IQueryResult qr, int expectedResult) {
    Async async = context.async();

    try {
      context.assertNotNull(qr);
    } catch (Exception e) {
      LOGGER.error("", e);
      async.complete();
      throw ExceptionUtil.createRuntimeException(e);
    }

    if (expectedResult < 0) {
      async.complete();
      return;
    }
    if (expectedResult == 0) {
      try {
        context.assertFalse(qr.iterator().hasNext());
      } finally {
        async.complete();
      }
    } else {
      try {
        context.assertEquals(expectedResult, qr.size(), "Not the expected number of records");
      } catch (Exception e) {
        LOGGER.error("", e);
        async.complete();
        throw ExceptionUtil.createRuntimeException(e);
      }

      IteratorAsync<?> itr = qr.iterator();
      try {
        context.assertTrue(itr.hasNext(), "No record in selection");
      } catch (Exception e) {
        LOGGER.error("", e);
        async.complete();
        throw ExceptionUtil.createRuntimeException(e);
      }

      itr.next(nitr -> {
        try {
          if (nitr.failed()) {
            LOGGER.error("", nitr.cause());
            throw ExceptionUtil.createRuntimeException(nitr.cause());
          }
          context.assertNotNull(nitr.result());
        } finally {
          async.complete();
        }
      });
    }

    async.await();
  }

  /**
   * Calls {@link IDatastoreContainer#clearTable(String, io.vertx.core.Handler)} and waits for it.
   * 
   * @param context
   * @param tableName
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void clearTable(TestContext context, Class mapperClass) {
    if (EXTERNAL_DATASTORE != null) {
      Async async = context.async();
      ErrorObject<Void> err = new ErrorObject<Void>(null);
      IQuery q = EXTERNAL_DATASTORE.createQuery(mapperClass);
      find(context, q, -1).queryResult.toArray(result -> {
        if (result.failed()) {
          err.setThrowable(result.cause());
          async.complete();
        } else {
          if (result.result().length > 0) {
            IDelete delete = EXTERNAL_DATASTORE.createDelete(mapperClass);
            delete.add(result.result());
            delete.delete(delResult -> {
              AsyncResult<IDeleteResult> dr = (AsyncResult<IDeleteResult>) delResult;
              if (dr.failed()) {
                err.setThrowable(dr.cause());
                async.complete();
              } else {
                async.complete();
              }
            });
          } else {
            async.complete();
          }
        }
      });
      async.await();
      if (err.isError())
        throw err.getRuntimeException();
    } else {
      clearTable(context, mapperClass.getSimpleName());
    }

  }

  /**
   * Calls {@link IDatastoreContainer#clearTable(String, io.vertx.core.Handler)} and waits for it.
   * 
   * @param context
   * @param tableName
   */
  public static void clearTable(TestContext context, String tableName) {
    LOGGER.info("clearing table " + tableName);
    Async async = context.async();
    ErrorObject<Void> err = new ErrorObject<Void>(null);
    TestHelper.getDatastoreContainer(context).clearTable(tableName, result -> {
      if (result.failed()) {
        err.setThrowable(result.cause());
      }
      async.complete();
    });

    async.await();
    LOGGER.info("finished clearing table " + tableName);
    if (err.isError())
      throw err.getRuntimeException();
  }

  public static void dropTable(TestContext context, String tableName) {
    Async async = context.async();
    ErrorObject<Void> err = new ErrorObject<Void>(null);
    TestHelper.getDatastoreContainer(context).dropTable(tableName, result -> {
      if (result.failed()) {
        err.setThrowable(result.cause());
      }
      async.complete();
    });

    async.await();
    if (err.isError())
      throw err.getRuntimeException();
  }

  /**
   * Validates the existence of an index
   * 
   * @param context
   * @param q
   */
  protected void checkIndex(TestContext context, IMapper mapper, String indexName) {
    Async async = context.async();
    getDataStore(context).getMetaData().getIndexInfo(indexName, mapper, result -> {
      if (result.failed()) {
        context.fail(result.cause());
        async.complete();
      } else {
        Object indexInfo = result.result();
        LOGGER.info("indexInfo: " + indexInfo);
        context.assertNotNull(indexInfo, "Index wasn't created");
        async.complete();
      }
    });
    async.await();
  }

}
