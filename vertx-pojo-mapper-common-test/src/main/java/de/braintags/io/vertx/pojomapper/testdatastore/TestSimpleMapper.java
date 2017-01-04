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

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.FieldCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.SimpleMapper;
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
  private static Logger logger = LoggerFactory.getLogger(TestSimpleMapper.class);
  private static boolean dropTable = false;

  @Test
  public void testSimpleMapper(TestContext context) {
    clearTable(context, "SimpleMapper");
    SimpleMapper sm = new SimpleMapper();
    sm.name = "testName";
    sm.setSecondProperty("my second property");
    ResultContainer resultContainer = saveRecord(context, sm);
    IWriteEntry we = resultContainer.writeResult.iterator().next();
    context.assertEquals(we.getAction(), WriteAction.INSERT);
    context.assertNotNull(sm.id);
    context.assertTrue(sm.id.hashCode() != 0); // "ID wasn't set by insert statement",
    logger.info("ID is: " + sm.id);

    sm.name = "testNameModified";
    sm.setSecondProperty("my modified property");
    resultContainer = saveRecord(context, sm);
    we = resultContainer.writeResult.iterator().next();
    context.assertEquals(we.getAction(), WriteAction.UPDATE);

    // SimpleQuery for all records
    IQuery<SimpleMapper> query = getDataStore(context).createQuery(SimpleMapper.class);
    resultContainer = find(context, query, 1);

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        logger.error("", result.cause());
        context.fail(result.cause().toString());
      } else {
        context.assertTrue(sm.equals(result.result()));

        // search inside name field
        query.setRootQueryPart(new FieldCondition("name", "testNameModified"));
        try {
          ResultContainer resultContainer2 = find(context, query, 1);
          resultContainer2.queryResult.iterator().next(res2 -> {
            if (res2.failed()) {
              logger.error("", result.cause());
              context.fail(result.cause().toString());
            } else {
              SimpleMapper rsm = (SimpleMapper) result.result();
              context.assertTrue(sm.equals(rsm));
              context.assertEquals("succeeded", rsm.beforeSave);
              context.assertEquals("succeeded", rsm.afterSave);
              context.assertEquals("succeeded", rsm.afterLoad);
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
