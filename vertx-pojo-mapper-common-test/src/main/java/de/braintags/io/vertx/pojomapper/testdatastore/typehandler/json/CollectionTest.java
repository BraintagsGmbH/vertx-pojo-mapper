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
package de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.CollectionRecord;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class CollectionTest extends AbstractTypeHandlerTest {

  @Test
  public void extreme(TestContext context) {
    clearTable(context, CollectionRecord.class.getSimpleName());
    CollectionRecord record = new CollectionRecord();
    record.collection = null;
    saveRecord(context, record);
    IQuery<CollectionRecord> query = getDataStore(context).createQuery(CollectionRecord.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    CollectionRecord loaded = (CollectionRecord) list.get(0);
    context.assertNull(loaded.collection);

    record.collection = new ArrayList<>();
    saveRecord(context, record);
    query = getDataStore(context).createQuery(CollectionRecord.class);
    list = findAll(context, query);
    context.assertEquals(1, list.size());
    loaded = (CollectionRecord) list.get(0);
    context.assertNotNull(loaded.collection);
    context.assertEquals(0, loaded.collection.size());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    return new CollectionRecord();
  }

  @Override
  protected String getTestFieldName() {
    return "collection";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.CollectionTypeHandler";
  }

}
