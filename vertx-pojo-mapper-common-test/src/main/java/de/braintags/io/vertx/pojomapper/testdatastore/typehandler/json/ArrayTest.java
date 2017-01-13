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
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.ArrayRecord;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class ArrayTest extends AbstractTypeHandlerTest {

  @Test
  public void extreme(TestContext context) {
    clearTable(context, ArrayRecord.class.getSimpleName());
    ArrayRecord record = new ArrayRecord();
    record.array = null;
    record.arrayWithEqualValues = new String[0];
    saveRecord(context, record);
    IQuery<ArrayRecord> query = getDataStore(context).createQuery(ArrayRecord.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    ArrayRecord loaded = (ArrayRecord) list.get(0);
    context.assertNull(loaded.array);
    context.assertNotNull(loaded.arrayWithEqualValues);
    context.assertTrue(record.arrayWithEqualValues.length == 0);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    return new ArrayRecord();
  }

  @Override
  protected String getTestFieldName() {
    return "array";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.ArrayTypeHandler";
  }

}
