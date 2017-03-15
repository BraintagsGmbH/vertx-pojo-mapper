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

import java.util.List;

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.MapRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.ObjectNodeRecord;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class ObjectNodeTest extends AbstractDatatypeTest {

  public ObjectNodeTest() {
    super("map");
  }

  @Test
  public void extreme(TestContext context) {
    clearTable(context, ObjectNodeRecord.class.getSimpleName());
    ObjectNodeRecord record = new ObjectNodeRecord();
    record.setObjectNode(null);
    saveRecord(context, record);
    IQuery<ObjectNodeRecord> query = getDataStore(context).createQuery(ObjectNodeRecord.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    ObjectNodeRecord loaded = (ObjectNodeRecord) list.get(0);
    context.assertNull(loaded.getObjectNode());

    record.setObjectNode(Json.mapper.createObjectNode());
    saveRecord(context, record);
    query = getDataStore(context).createQuery(ObjectNodeRecord.class);
    list = findAll(context, query);
    context.assertEquals(1, list.size());
    loaded = (ObjectNodeRecord) list.get(0);
    context.assertNotNull(loaded.getObjectNode());
    context.assertEquals(0, loaded.getObjectNode().size());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    return new MapRecord();
  }

}
