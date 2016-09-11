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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.NumericMapper;
import io.vertx.ext.unit.TestContext;

/**
 * Mapper for testing numeric values
 * 
 * @author Michael Remme
 * 
 */
public class NumericTest extends AbstractTypeHandlerTest {

  @Test
  public void extreme(TestContext context) {
    clearTable(context, NumericMapper.class.getSimpleName());
    NumericMapper record = new NumericMapper();
    record.bigDecimal = null;
    record.bigInteger = null;
    record.myDouble = null;
    record.myFloatOb = null;
    record.myInteger = null;
    record.myLongOb = null;
    record.myShort = null;
    saveRecord(context, record);
    IQuery<NumericMapper> query = getDataStore(context).createQuery(NumericMapper.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    NumericMapper loaded = (NumericMapper) list.get(0);
    context.assertNull(loaded.bigDecimal);
    context.assertNull(loaded.bigInteger);
    context.assertNull(loaded.myDouble);
    context.assertNull(loaded.myFloatOb);
    context.assertNull(loaded.myInteger);
    context.assertNull(loaded.myLongOb);
    context.assertNull(loaded.myShort);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    NumericMapper mapper = new NumericMapper();
    mapper.bigDecimal = new BigDecimal(23.44);
    mapper.bigInteger = new BigInteger("9987");
    mapper.myDoub = 333.5555;
    mapper.myDouble = new Double(678.1234);
    mapper.myFloat = 34.22f;
    mapper.myFloatOb = new Float(234.45);
    mapper.myInt = 45;
    mapper.myInteger = new Integer(67);
    mapper.myLong = 78934;
    mapper.myLongOb = new Long(78797);
    mapper.mySh = 34;
    mapper.myShort = new Short((short) 45);
    return mapper;
  }

  @Override
  protected String getTestFieldName() {
    return "myLong";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.LongTypeHandler";
  }

}
