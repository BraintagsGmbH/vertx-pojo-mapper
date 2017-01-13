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
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.EmbeddedListMapper;
import io.vertx.ext.unit.TestContext;

/**
 * Mapper for testing boolean values
 * 
 * @author Michael Remme
 * 
 */
public class EmbeddedListTest extends AbstractTypeHandlerTest {

  @Test
  public void extreme(TestContext context) {
    clearTable(context, EmbeddedListMapper.class.getSimpleName());
    EmbeddedListMapper record = new EmbeddedListMapper();
    record.dateTestList = null;
    saveRecord(context, record);
    IQuery<EmbeddedListMapper> query = getDataStore(context).createQuery(EmbeddedListMapper.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    EmbeddedListMapper loaded = (EmbeddedListMapper) list.get(0);
    context.assertNull(loaded.dateTestList);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    BaseRecord mapper = new EmbeddedListMapper();
    return mapper;
  }

  @Override
  protected String getTestFieldName() {
    return "stringTestList";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.CollectionTypeHandlerEmbedded";
  }

}
