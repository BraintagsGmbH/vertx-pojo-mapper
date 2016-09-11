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

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.MapRecord;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MapTest extends AbstractTypeHandlerTest {

  @Test
  public void extreme(TestContext context) {
    clearTable(context, MapRecord.class.getSimpleName());
    MapRecord record = new MapRecord();
    record.map = null;
    saveRecord(context, record);
    IQuery<MapRecord> query = getDataStore(context).createQuery(MapRecord.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    MapRecord loaded = (MapRecord) list.get(0);
    context.assertNull(loaded.map);

    record.map = new HashMap<>();
    saveRecord(context, record);
    query = getDataStore(context).createQuery(MapRecord.class);
    list = findAll(context, query);
    context.assertEquals(1, list.size());
    loaded = (MapRecord) list.get(0);
    context.assertNotNull(loaded.map);
    context.assertEquals(0, loaded.map.size());

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    return new MapRecord();
  }

  @Override
  protected String getTestFieldName() {
    return "map";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.MapTypeHandler";
  }

}
