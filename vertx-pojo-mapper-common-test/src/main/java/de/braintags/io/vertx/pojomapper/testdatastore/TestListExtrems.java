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
  public void testWriteRead_Unnotated1(TestContext context) {
    ListMapperNoAnnotation sm = new ListMapperNoAnnotation();
    saveAndRead(context, sm, true);
  }

  @Test
  public void testWriteRead_Unnotated2(TestContext context) {
    ListMapperNoAnnotation sm = new ListMapperNoAnnotation();
    sm.simplemapper = null;
    saveAndRead(context, sm, true);
  }

  @Test
  public void testWriteRead_Unnotated3(TestContext context) {
    ListMapperNoAnnotation sm = new ListMapperNoAnnotation(5);
    saveAndRead(context, sm, true);
  }

  protected void saveAndRead(TestContext context, ListMapperNoAnnotation sm, boolean clearTable) {
    if (clearTable) {
      clearTable(context, ListMapperNoAnnotation.class.getSimpleName());
    }
    saveRecord(context, sm);
    IQuery<ListMapperNoAnnotation> query = getDataStore(context).createQuery(ListMapperNoAnnotation.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    ListMapperNoAnnotation loaded = (ListMapperNoAnnotation) list.get(0);
    context.assertEquals(sm.simplemapper, loaded.simplemapper);
  }

  /**
   * Save record with embedded list with two entries, delete one entry, save again, load entity and compare;
   * delete again -> compare
   * 
   * @param context
   */
  @Test
  public void testDeleteListEntryAndUpdate(TestContext context) {
    ListMapperNoAnnotation sm = new ListMapperNoAnnotation(2);
    saveAndRead(context, sm, true);
    sm.simplemapper.remove(0);
    saveAndRead(context, sm, false);
    sm.simplemapper.remove(0);
    saveAndRead(context, sm, false);
  }

}
