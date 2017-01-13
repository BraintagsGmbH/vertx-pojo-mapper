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
import de.braintags.io.vertx.pojomapper.testdatastore.ResultContainer;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.EmbeddedMapper_Map;
import io.vertx.ext.unit.TestContext;

/**
 * Tests for testing embedded Maps
 * 
 * @author Michael Remme
 * 
 */
public class EmbeddedMapTest extends AbstractTypeHandlerTest {

  @Test
  public void extreme(TestContext context) {
    clearTable(context, EmbeddedMapper_Map.class.getSimpleName());
    EmbeddedMapper_Map record = new EmbeddedMapper_Map();
    record.simpleMapper = null;
    saveRecord(context, record);
    IQuery<EmbeddedMapper_Map> query = getDataStore(context).createQuery(EmbeddedMapper_Map.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    EmbeddedMapper_Map loaded = (EmbeddedMapper_Map) list.get(0);
    context.assertNull(loaded.simpleMapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    BaseRecord mapper = new EmbeddedMapper_Map();
    return mapper;
  }

  @Override
  protected String getTestFieldName() {
    return "simpleMapper";
  }

  @Override
  protected void validateAfterSave(TestContext context, Object record, ResultContainer resultContainer) {
    super.validateAfterSave(context, record, resultContainer);
    EmbeddedMapper_Map mapper = (EmbeddedMapper_Map) record;
    context.assertNotNull(mapper.id);
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.MapTypeHandlerEmbedded";
  }

}
