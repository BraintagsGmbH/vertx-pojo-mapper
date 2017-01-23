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

import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.CalendarMapper;
import io.vertx.ext.unit.TestContext;

/**
 * Mapper for testing {@link Calendar} values
 * 
 * @author Michael Remme
 * 
 */
public class CalendarTest extends AbstractTypeHandlerTest {

  @Test
  public void extreme(TestContext context) {
    clearTable(context, CalendarMapper.class.getSimpleName());
    CalendarMapper record = new CalendarMapper();
    record.myCal = null;
    saveRecord(context, record);
    IQuery<CalendarMapper> query = getDataStore(context).createQuery(CalendarMapper.class);
    List<CalendarMapper> list = findAll(context, query);
    context.assertEquals(1, list.size());
    CalendarMapper loaded = list.get(0);
    // currenty broken, since by default, Timestamp fields can not be null in MySQL
    context.assertNull(loaded.myCal, "Found " + loaded.myCal);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    CalendarMapper mapper = new CalendarMapper();
    return mapper;
  }

  @Override
  protected String getTestFieldName() {
    return "myCal";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.vertx.jomnigate.json.typehandler.handler.CalendarTypeHandler";
  }

}
