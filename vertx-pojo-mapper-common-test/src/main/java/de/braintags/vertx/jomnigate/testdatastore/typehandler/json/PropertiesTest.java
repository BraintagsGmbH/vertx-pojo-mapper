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
package de.braintags.vertx.jomnigate.testdatastore.typehandler.json;

import java.util.List;
import java.util.Properties;

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.PropertiesRecord;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class PropertiesTest extends AbstractTypeHandlerTest {

  @Test
  public void testJsonArray(TestContext context) {
    JsonObject jo = new JsonObject();
    JsonArray array = new JsonArray();
    array.add(true);
    jo.put("array1", jo);
    JsonArray array2 = new JsonArray();
    jo.put("array2", array2);
  }

  @Test
  public void extreme(TestContext context) {
    clearTable(context, PropertiesRecord.class.getSimpleName());
    PropertiesRecord record = new PropertiesRecord();
    record.properties = null;
    saveRecord(context, record);
    IQuery<PropertiesRecord> query = getDataStore(context).createQuery(PropertiesRecord.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    PropertiesRecord loaded = (PropertiesRecord) list.get(0);
    context.assertNull(loaded.properties);

    record.properties = new Properties();
    saveRecord(context, record);
    query = getDataStore(context).createQuery(PropertiesRecord.class);
    list = findAll(context, query);
    context.assertEquals(1, list.size());
    loaded = (PropertiesRecord) list.get(0);
    context.assertNotNull(loaded.properties);
    context.assertEquals(0, loaded.properties.size());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    return new PropertiesRecord();
  }

  @Override
  protected String getTestFieldName() {
    return "properties";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.vertx.jomnigate.json.typehandler.handler.MapTypeHandler";
  }

}
