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
package de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json;

import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.ReferenceMapper_Map;
import io.vertx.ext.unit.TestContext;

/**
 * Tests for testing embedded Maps
 * 
 * @author Michael Remme
 * 
 */
public class ReferencedMapTest extends AbstractTypeHandlerTest {

  @Test
  public void extreme(TestContext context) {
    clearTable(context, ReferenceMapper_Map.class.getSimpleName());
    ReferenceMapper_Map record = new ReferenceMapper_Map();
    record.simpleMapper = null;
    saveRecord(context, record);
    IQuery<ReferenceMapper_Map> query = getDataStore(context).createQuery(ReferenceMapper_Map.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    ReferenceMapper_Map loaded = (ReferenceMapper_Map) list.get(0);
    context.assertNull(loaded.simpleMapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    BaseRecord mapper = new ReferenceMapper_Map(5);
    return mapper;
  }

  @Override
  protected String getTestFieldName() {
    return "simpleMapper";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.MapTypeHandlerReferenced";
  }

}
