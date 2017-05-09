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

package de.braintags.vertx.jomnigate.testdatastore;

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteEntry;
import de.braintags.vertx.jomnigate.dataaccess.write.WriteAction;
import de.braintags.vertx.jomnigate.testdatastore.mapper.SimpleMapper_NO_INDEX;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TestSimpleMapper extends DatastoreBaseTest {
  private static final String TEST_NAME_MODIFIED = "testNameModified";
  private static final String SUCCEEDED = "succeeded";
  private static Logger logger = LoggerFactory.getLogger(TestSimpleMapper.class);
  private static boolean dropTable = false;

  @Test
  public void findById(final TestContext context) {
    clearTable(context, "SimpleMapper");
    SimpleMapper_NO_INDEX sm = new SimpleMapper_NO_INDEX();
    sm.name = "testName";
    sm.setSecondProperty("my second property");
    context.assertNull(sm.id);
    ResultContainer resultContainer = saveRecord(context, sm);
    IWriteEntry we = resultContainer.writeResult.iterator().next();
    context.assertEquals(WriteAction.INSERT, we.getAction());
    context.assertNotNull(we.getStoreObject());

    context.assertNotNull(sm.id);
    context.assertTrue(sm.id.hashCode() != 0, "ID wasn't set by insert statement");
    logger.info("ID is: " + sm.id);

    // SimpleQuery for all records
    IQuery<SimpleMapper_NO_INDEX> query = getDataStore(context).createQuery(SimpleMapper_NO_INDEX.class);
    query.setSearchCondition(ISearchCondition.isEqual(query.getMapper().getIdInfo().getIndexedField(), sm.id));
    resultContainer = find(context, query, 1);
  }

  @Test
  public void testSimpleMapper(final TestContext context) {
    clearTable(context, "SimpleMapper");
    SimpleMapper_NO_INDEX sm = new SimpleMapper_NO_INDEX();
    sm.name = "testName";
    sm.setSecondProperty("my second property");
    context.assertNull(sm.id);
    ResultContainer resultContainer = saveRecord(context, sm);
    IWriteEntry we = resultContainer.writeResult.iterator().next();
    context.assertEquals(WriteAction.INSERT, we.getAction());
    context.assertNotNull(we.getStoreObject());
    context.assertNotNull(we.getStoreObject());

    context.assertNotNull(sm.id);
    context.assertTrue(sm.id.hashCode() != 0, "ID wasn't set by insert statement");
    logger.info("ID is: " + sm.id);

    sm.name = TEST_NAME_MODIFIED;
    sm.setSecondProperty("my modified property");
    resultContainer = saveRecord(context, sm);
    we = resultContainer.writeResult.iterator().next();
    context.assertEquals(WriteAction.UPDATE, we.getAction());

    // SimpleQuery for all records
    IQuery<SimpleMapper_NO_INDEX> query = getDataStore(context).createQuery(SimpleMapper_NO_INDEX.class);
    resultContainer = find(context, query, 1);

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        logger.error("", result.cause());
        context.fail(result.cause().toString());
      } else {
        SimpleMapper_NO_INDEX sm2 = (SimpleMapper_NO_INDEX) result.result();
        context.assertEquals(TEST_NAME_MODIFIED, sm2.name, "record was not updated");

        context.assertTrue(sm.equals(result.result()));

        // search inside name field
        query.setSearchCondition(ISearchCondition.isEqual("name", TEST_NAME_MODIFIED));
        try {
          ResultContainer resultContainer2 = find(context, query, 1);
          resultContainer2.queryResult.iterator().next(res2 -> {
            if (res2.failed()) {
              logger.error("", result.cause());
              context.fail(result.cause().toString());
            } else {
              SimpleMapper_NO_INDEX rsm = (SimpleMapper_NO_INDEX) result.result();
              context.assertTrue(sm.equals(rsm));
              context.assertEquals(SUCCEEDED, rsm.beforeSave);
              context.assertEquals(SUCCEEDED, rsm.afterSave);
              context.assertEquals(SUCCEEDED, rsm.afterLoad);
            }
          });
        } catch (Throwable e) {
          logger.error("", result.cause());
          context.fail(e);
        }
      }
    });
  }

}
