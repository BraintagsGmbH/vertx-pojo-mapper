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

import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.CalendarMapper;
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
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    CalendarMapper loaded = (CalendarMapper) list.get(0);
    context.assertNull(loaded.myCal);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
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
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.CalendarTypeHandler";
  }

}
