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

import org.junit.Test;

import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.StringBufferTestMapper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.StringTestMapper;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class StringTest extends AbstractDatatypeTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(StringTest.class);

  public StringTest() {
    super("stringField");
  }

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

  @Test
  public final void testStringBuffer(TestContext context) {
    try {
      StringBufferTestMapper mapper = new StringBufferTestMapper(2);
      saveLoadAndCompare(context, mapper);
    } catch (Exception e) {
      LOGGER.warn("StringBuffer not supported", e);
    }

  }

  private void saveLoadAndCompare(TestContext context, StringBufferTestMapper mapper) {
    saveRecord(context, mapper);
    StringBufferTestMapper loaded = (StringBufferTestMapper) findRecordByID(context, mapper.getClass(), mapper.id);
    context.assertEquals(mapper.stringBufferField.getClass(), loaded.stringBufferField.getClass());
    context.assertEquals(mapper.stringBufferField.toString(), loaded.stringBufferField.toString());
  }

  private void saveLoadAndCompare(TestContext context, StringTestMapper mapper) {
    saveRecord(context, mapper);
    StringTestMapper loaded = (StringTestMapper) findRecordByID(context, mapper.getClass(), mapper.id);
    context.assertEquals(mapper.stringField, loaded.stringField);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
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

}
