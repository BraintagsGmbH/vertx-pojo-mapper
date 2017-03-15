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
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.DateMapper;
import io.vertx.ext.unit.TestContext;

/**
 * Mapper for testing boolean values
 * 
 * @author Michael Remme
 * 
 */
public class DateTest extends AbstractDatatypeTest {

  public DateTest() {
    super("sqlDate");
  }

  @Test
  public void extreme(TestContext context) {
    clearTable(context, DateMapper.class.getSimpleName());
    DateMapper record = new DateMapper();
    record.javaDate = null;
    record.myTime = null;
    record.myTimeStamp = null;
    record.sqlDate = null;
    record.jodaDate = null;
    saveRecord(context, record);
    IQuery<DateMapper> query = getDataStore(context).createQuery(DateMapper.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    DateMapper loaded = (DateMapper) list.get(0);
    context.assertNull(loaded.javaDate);
    context.assertNull(loaded.myTime);
    context.assertNull(loaded.myTimeStamp);
    context.assertNull(loaded.sqlDate);
    context.assertNull(loaded.jodaDate);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    DateMapper mapper = new DateMapper();
    return mapper;
  }

}
