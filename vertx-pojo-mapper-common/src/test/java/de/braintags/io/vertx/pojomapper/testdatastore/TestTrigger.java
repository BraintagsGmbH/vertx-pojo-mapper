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
package de.braintags.io.vertx.pojomapper.testdatastore;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.TriggerMapper;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TestTrigger extends DatastoreBaseTest {
  private static final Logger log = LoggerFactory.getLogger(TestTrigger.class);

  @Test
  public void testAllTriggers(TestContext context) {
    clearTable(context, "TriggerMapper");

    TriggerMapper tm = new TriggerMapper();
    context.assertEquals("testName", tm.name);
    ResultContainer resultContainer = saveRecord(context, tm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
    context.assertEquals("beforeSave", tm.name);
    context.assertEquals("beforeSaveWithDataStore", tm.beforeSaveWithDataStore);

    context.assertEquals("afterSave", tm.afterSave);
    context.assertEquals("afterSaveWithDataStore", tm.afterSaveWithDataStore);

    IQuery<TriggerMapper> query = getDataStore().createQuery(TriggerMapper.class);
    tm = (TriggerMapper) findFirst(context, query);
    context.assertEquals("afterLoad", tm.afterLoad);
    context.assertEquals("afterLoadWithDatastore", tm.afterLoadWithDatastore);

    IDelete<TriggerMapper> del = getDataStore().createDelete(TriggerMapper.class);
    del.add(tm);
    delete(context, del, query, 0);
    context.assertEquals("afterDelete", tm.afterDelete);
    context.assertEquals("afterDeleteWithDatastore", tm.afterDeleteWithDatastore);

    context.assertEquals("beforeDelete", tm.beforeDelete);
    context.assertEquals("beforeDeleteWithDatastore", tm.beforeDeleteWithDatastore);

  }

}
