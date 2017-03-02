/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.testdatastore;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.init.IDataStoreInit;
import de.braintags.vertx.jomnigate.testdatastore.mapper.SimpleMapper;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

/**
 * Unit test for {@link DataStoreSettings#isClearDatabaseOnInit()}
 * 
 * @author sschmitt
 * 
 */
@RunWith(VertxUnitRunner.class)
public class TestClearDatastore {

  /**
   * Ensure that records remain between datastore initialziations if 'clearDatabaseOnInit' is false
   * 
   * @param context
   */
  @Test
  public void testDatastore_noClearOnInit(TestContext context) {
    testDatastore(false, context);
  }

  /**
   * Ensure that records remain between datastore initialziations if 'clearDatabaseOnInit' is true
   * 
   * @param context
   */
  @Test
  public void testDatastore_clearOnInit(TestContext context) {
    testDatastore(true, context);
  }

  /**
   * Initializes a datastore and writes a record to it. Aftewards, initializes a second datastore with the same settings
   * and checks if the record is still there.
   * If shouldClear is 'true', it should not be there anymore, otherwise it must still be there.
   * 
   * @param shouldClear
   *          value of the 'clearDatabaseOnInit' setting
   * @param context
   *          the test context
   */
  private void testDatastore(boolean shouldClear, TestContext context) {
    DataStoreSettings settings = TestHelper.getDatastoreContainer(context).createSettings();
    settings.setClearDatabaseOnInit(shouldClear);

    IDataStoreInit init;
    try {
      init = settings.getDatastoreInit().newInstance();
    } catch (InstantiationException | IllegalAccessException e1) {
      context.fail(e1);
      return;
    }

    init.initDataStore(TestHelper.vertx, settings, context.asyncAssertSuccess((IDataStore datastore) -> {
      IWrite<SimpleMapper> write = datastore.createWrite(SimpleMapper.class);
      SimpleMapper mapper = new SimpleMapper();
      mapper.name = "Test";
      write.add(mapper);
      write.save(context.asyncAssertSuccess((IWriteResult writeResult) -> {
        IDataStoreInit init2;
        try {
          init2 = settings.getDatastoreInit().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          context.fail(e);
          return;
        }

        init2.initDataStore(TestHelper.vertx, settings, context.asyncAssertSuccess((IDataStore datastore2) -> {
          IQuery<SimpleMapper> query = datastore2.createQuery(SimpleMapper.class);
          query.setSearchCondition(ISearchCondition.isEqual("id", mapper.id));
          query.execute(context.asyncAssertSuccess(queryResult -> {
            context.assertEquals(queryResult.iterator().hasNext(), !shouldClear,
                "The record saved on one datastore should " + (shouldClear ? "not" : "still")
                    + " be accessible on a new datastore if 'clearDatabaseOnInit' is " + shouldClear);
          }));
        }));
      }));
    }));
  }
}
