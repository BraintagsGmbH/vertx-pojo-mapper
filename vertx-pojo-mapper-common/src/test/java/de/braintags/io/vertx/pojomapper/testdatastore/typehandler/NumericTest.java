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
package de.braintags.io.vertx.pojomapper.testdatastore.typehandler;

import java.math.BigDecimal;
import java.math.BigInteger;

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
