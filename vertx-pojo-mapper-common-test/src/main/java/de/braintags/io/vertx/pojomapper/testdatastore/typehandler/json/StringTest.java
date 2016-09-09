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

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.StringTestMapper;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class StringTest extends AbstractTypeHandlerTest {

  @Test
  public final void testExtrems(TestContext context) {
    StringTestMapper mapper = new StringTestMapper();
    mapper.stringField = null;
    saveLoadAndCompare(context, mapper);
  }

  @Test
  public final void testExtrems2(TestContext context) {
    StringTestMapper mapper = new StringTestMapper();
    mapper.stringField = "testtect";
    saveLoadAndCompare(context, mapper);
    mapper.stringField = "";
    saveLoadAndCompare(context, mapper);
    mapper.stringField = null;
    saveLoadAndCompare(context, mapper);

  }

  private void saveLoadAndCompare(TestContext context, StringTestMapper mapper) {
    saveRecord(context, mapper);
    StringTestMapper loaded = (StringTestMapper) findRecordByID(context, mapper.getClass(), mapper.id);
    context.assertEquals(mapper.stringField, loaded.stringField);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    StringTestMapper mapper = new StringTestMapper();
    mapper.stringField = "testcontent";
    return mapper;
  }

  @Override
  protected String getTestFieldName() {
    return "stringField";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.StringTypeHandler";
  }

}
