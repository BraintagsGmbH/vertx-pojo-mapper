/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.dataaccess.datatypetests;

import java.util.Iterator;

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteEntry;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import de.braintags.vertx.jomnigate.testdatastore.DatastoreBaseTest;
import de.braintags.vertx.jomnigate.testdatastore.ResultContainer;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
import de.braintags.vertx.util.ExceptionUtil;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractDatatypeTest extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractDatatypeTest.class);
  private String testFieldName;

  public AbstractDatatypeTest(String testFieldName) {
    this.testFieldName = testFieldName;
  }

  /**
   * Get the name of the field, which for the {@link ITypeHandler} shall be checked
   * 
   * @return the name of the field
   */
  protected String getTestFieldName() {
    return testFieldName;
  }

  @Test
  public final void testSaveAndReadRecord(TestContext context) {
    testSaveAndReadRecord(context, createInstance(context));
  }

  protected void testSaveAndReadRecord(TestContext context, BaseRecord record) {
    try {
      dropTables(context, record);

      ResultContainer resultContainer = saveRecord(context, record);
      validateAfterSave(context, record, resultContainer);
      context.assertNotNull(resultContainer.writeResult, "write result is null");

      Iterator<IWriteEntry> it = resultContainer.writeResult.iterator();
      while (it.hasNext()) {
        IWriteEntry we = it.next();
        IStoreObject<?, ?> entry = we.getStoreObject();
        LOGGER.info("written entry: " + entry.toString() + " | " + we.getAction());
      }

      // SimpleQuery for all records
      IQuery<? extends BaseRecord> query = getDataStore(context).createQuery(record.getClass());
      resultContainer = find(context, query, 1);
      verifyResult(context, record, resultContainer);
      resultContainer.queryResult.iterator().next(result -> {
        if (result.failed()) {
          result.cause().printStackTrace();
        } else {
          context.assertTrue(record.equals(result.result()));
          LOGGER.info("finished!");
        }
      });
    } catch (Exception e) {
      LOGGER.info("", e);
      throw ExceptionUtil.createRuntimeException(e);
    }
  }

  /**
   * Validates a record after it was written into the datastore
   * 
   * @param context
   * @param record
   * @param resultContainer
   */
  protected void validateAfterSave(TestContext context, Object record, ResultContainer resultContainer) {
    context.assertNotNull(resultContainer, "resultContainer must not be null");
  }

  /**
   * Verifies the result of the query
   * 
   * @param context
   * @param record
   * @param resultContainer
   * @throws AssertionError
   */
  protected void verifyResult(TestContext context, BaseRecord record, ResultContainer resultContainer)
      throws AssertionError {

  }

  protected void dropTables(TestContext context, BaseRecord record) {
    clearTable(context, record.getClass().getSimpleName());
    clearTable(context, "SimpleMapper");
  }

  /**
   * Create a record to be used to test the {@link ITypeHandler}
   * 
   * @param context
   *          the {@link TestContext}
   * @return
   */
  public abstract BaseRecord createInstance(TestContext context);
}
