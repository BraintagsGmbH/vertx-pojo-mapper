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

import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.ListMapperNoAnnotation;
import io.vertx.ext.unit.TestContext;

/**
 * Test for embedded object without annotation
 * 
 * @author Michael Remme
 * 
 */
public class TestListExtrems extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestListExtrems.class);

  @Test
  public void testWriteRead1(TestContext context) {
    clearTable(context, ListMapperNoAnnotation.class.getSimpleName());
    ListMapperNoAnnotation sm = new ListMapperNoAnnotation();
    saveRecord(context, sm);
    IQuery<ListMapperNoAnnotation> query = getDataStore(context).createQuery(ListMapperNoAnnotation.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    ListMapperNoAnnotation loaded = (ListMapperNoAnnotation) list.get(0);
    context.assertEquals(sm.simpleMapper, loaded.simpleMapper);
  }

  /**
   * Save record with embedded list with two entries, delete one entry, save again, load entity and compare;
   * delete again -> compare
   * 
   * @param context
   */
  @Test
  public void testDeleteListEntryAndUpdate(TestContext context) {
    context.fail("unimplemented");
  }

}
