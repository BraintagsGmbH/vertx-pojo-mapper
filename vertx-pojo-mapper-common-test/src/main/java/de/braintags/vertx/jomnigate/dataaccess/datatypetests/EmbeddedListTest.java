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
import de.braintags.vertx.jomnigate.testdatastore.ResultContainer;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.EmbeddedListMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.StringTestMapper;
import io.vertx.ext.unit.TestContext;

/**
 * Mapper for testing boolean values
 * 
 * @author Michael Remme
 * 
 */
public class EmbeddedListTest extends AbstractDatatypeTest {

  public EmbeddedListTest() {
    super("stringTestList");
  }

  @Override
  protected void validateAfterSave(TestContext context, Object record, ResultContainer resultContainer) {
    super.validateAfterSave(context, record, resultContainer);
    EmbeddedListMapper loaded = (EmbeddedListMapper) record;
    context.assertNotNull(loaded.stringTestList.iterator().next().id);
    loaded.stringTestList.add(new StringTestMapper(20));
    saveRecord(context, loaded);
  }

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
   * @see de.braintags.vertx.jomnigate.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    BaseRecord mapper = new EmbeddedListMapper();
    return mapper;
  }

}
