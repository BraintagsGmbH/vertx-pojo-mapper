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
package testtest;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.datastoretest.IDatastoreContainer;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler.ReferenceMapper_Array;
import de.braintags.io.vertx.pojomapper.exception.ParameterRequiredException;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStoreContainer;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlQueryResult;
import de.braintags.io.vertx.util.ErrorObject;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@RunWith(VertxUnitRunner.class)
public class TestAsAsyncMySql {
  private static final io.vertx.core.logging.Logger logger = io.vertx.core.logging.LoggerFactory
      .getLogger(TestAsAsyncMySql.class);

  protected static Vertx vertx;
  public static IDatastoreContainer datastoreContainer;

  @BeforeClass
  public static void setUp() {
    vertx = Vertx.vertx();
    System.setProperty("IDatastoreContainer", MySqlDataStoreContainer.class.getName());
  }

  @AfterClass
  public static void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  public void dropTable(String tableName, TestContext context) {
    Async async = context.async();
    ErrorObject<Void> err = new ErrorObject<Void>(null);
    datastoreContainer.dropTable(tableName, result -> {
      if (result.failed()) {
        err.setThrowable(result.cause());
      }
      async.complete();
    });

    async.await();
    if (err.isError())
      throw err.getRuntimeException();
  }

  @Before
  public void before(TestContext context) throws Exception {
    if (datastoreContainer == null) {
      Async async = context.async();
      logger.info("setup");
      String property = System.getProperty(IDatastoreContainer.PROPERTY);
      if (property == null) {
        throw new ParameterRequiredException("Need the parameter " + IDatastoreContainer.PROPERTY
            + ". Start the test with -DIDatastoreContainer=de.braintags.io.vertx.pojomapper.mysql.MySqlDataStoreContainer for instance");
      }
      datastoreContainer = (IDatastoreContainer) Class.forName(property).newInstance();
      ErrorObject<Void> err = new ErrorObject<Void>(null);
      logger.info("wait for startup of datastore");
      datastoreContainer.startup(vertx, result -> {
        if (result.failed()) {
          err.setThrowable(result.cause());
        }
        logger.info("datastore started");
        context.assertNotNull(getDataStore());
        async.complete();
      });

      async.await();
      if (err.isError())
        throw err.getRuntimeException();
    }
    dropTable("ReferenceMapper_Array", context);
  }

  public IDataStore getDataStore() {
    return datastoreContainer.getDataStore();
  }

  @Test
  public void testInsertAndQuery(TestContext context) {
    Async async = context.async();
    ErrorObject<Void> err = new ErrorObject<Void>(null);
    ReferenceMapper_Array mapper = new ReferenceMapper_Array(100);
    IWrite<ReferenceMapper_Array> write = getDataStore().createWrite(ReferenceMapper_Array.class);
    write.add(mapper);
    write.save(result -> {
      if (result.failed()) {
        err.setThrowable(result.cause());
        async.complete();
      } else {
        IQuery<ReferenceMapper_Array> query = getDataStore().createQuery(ReferenceMapper_Array.class);
        logger.info("executing query");
        query.execute(qr -> {
          if (qr.failed()) {
            err.setThrowable(qr.cause());
          }
          try {
            SqlQueryResult<ReferenceMapper_Array> mqr = (SqlQueryResult<ReferenceMapper_Array>) qr.result();
            logger.info(mqr.getOriginalQuery().toString());
            logger.info(mqr.getResultSet().toString());
            logger.info("size: " + qr.result().size());

          } catch (Exception e) {
            context.fail(e);
          } finally {
            async.complete();
          }

        });
      }

    });

    async.await();
    if (err.isError())
      context.fail(err.getThrowable());
  }

}
