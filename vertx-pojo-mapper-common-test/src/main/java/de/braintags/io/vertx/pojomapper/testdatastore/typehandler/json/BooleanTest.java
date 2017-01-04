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

import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BooleanMapper;
import io.vertx.ext.unit.TestContext;

/**
 * Mapper for testing boolean values
 * 
 * @author Michael Remme
 * 
 */
public class BooleanTest extends AbstractTypeHandlerTest {

  @Test
  public void extreme(TestContext context) {
    clearTable(context, BooleanMapper.class.getSimpleName());
    BooleanMapper record = new BooleanMapper();
    record.myBooloean = null;
    saveRecord(context, record);
    IQuery<BooleanMapper> query = getDataStore(context).createQuery(BooleanMapper.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    BooleanMapper loaded = (BooleanMapper) list.get(0);
    context.assertNull(loaded.myBooloean, "expected null, not " + loaded.myBooloean);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    BooleanMapper mapper = new BooleanMapper();
    mapper.myBool = false;
    mapper.myBooloean = new Boolean(true);
    return mapper;
  }

  @Override
  protected String getTestFieldName() {
    return "myBooloean";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectTypeHandler";
  }

}
