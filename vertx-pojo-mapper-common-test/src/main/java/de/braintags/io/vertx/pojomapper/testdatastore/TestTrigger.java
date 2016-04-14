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
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.MultipleTriggerMapper;
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
  public void testMultipleTriggers(TestContext context) {
    clearTable(context, MultipleTriggerMapper.class.getSimpleName());
    MultipleTriggerMapper source = new MultipleTriggerMapper("multi");
    ResultContainer resultContainer = saveRecord(context, source);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
    source.validate(context);

    IQuery<MultipleTriggerMapper> query = getDataStore(context).createQuery(MultipleTriggerMapper.class);
    MultipleTriggerMapper loaded = (MultipleTriggerMapper) findFirst(context, query);
    loaded.validate(context);

  }

  @Test
  public void testAllTriggers(TestContext context) {
    clearTable(context, "TriggerMapper");

    TriggerMapper source = new TriggerMapper();
    context.assertEquals("testName", source.name);
    ResultContainer resultContainer = saveRecord(context, source);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
    context.assertEquals("beforeSave", source.name);
    context.assertEquals("beforeSaveWithDataStore", source.beforeSaveWithDataStore);

    context.assertEquals("afterSave", source.afterSave);
    context.assertEquals("afterSaveWithDataStore", source.afterSaveWithDataStore);

    IQuery<TriggerMapper> query = getDataStore(context).createQuery(TriggerMapper.class);
    TriggerMapper loaded = (TriggerMapper) findFirst(context, query);
    context.assertEquals("afterLoad", loaded.afterLoad);
    context.assertEquals("afterLoadWithDatastore", loaded.afterLoadWithDatastore);
    // are the trigger contents saved?
    context.assertEquals("beforeSave", loaded.name);
    context.assertEquals("beforeSaveWithDataStore", loaded.beforeSaveWithDataStore);
    // after save is not saved
    TriggerMapper vorlage = new TriggerMapper();
    context.assertEquals(vorlage.afterSave, loaded.afterSave);
    context.assertEquals(vorlage.afterSaveWithDataStore, loaded.afterSaveWithDataStore);

    IDelete<TriggerMapper> del = getDataStore(context).createDelete(TriggerMapper.class);
    del.add(loaded);
    delete(context, del, query, 0);
    context.assertEquals("afterDelete", loaded.afterDelete);
    context.assertEquals("afterDeleteWithDatastore", loaded.afterDeleteWithDatastore);

    context.assertEquals("beforeDelete", loaded.beforeDelete);
    context.assertEquals("beforeDeleteWithDatastore", loaded.beforeDeleteWithDatastore);

  }

}
