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
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.EmbeddedMapper_NoKeyGenerator;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.EmbeddedMapper_Single;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.EmbeddedMapper_Single_Failure;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.SimpleMapperEmbedded;
import de.braintags.vertx.util.ExceptionUtil;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class EmbeddedSingleTest extends EmbeddedSingleTest_Null {

  @Override
  @Test
  public void extreme(TestContext context) {
    clearTable(context, EmbeddedMapper_Single.class.getSimpleName());
    EmbeddedMapper_Single record = new EmbeddedMapper_Single();
    record.simpleMapper = null;
    saveRecord(context, record);
    IQuery<EmbeddedMapper_Single> query = getDataStore(context).createQuery(EmbeddedMapper_Single.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    EmbeddedMapper_Single loaded = (EmbeddedMapper_Single) list.get(0);
    context.assertNull(loaded.simpleMapper);
  }

  @Test
  public void testWrongEmbedded(TestContext context) {
    try {
      ResultContainer r = saveRecord(context, new EmbeddedMapper_Single_Failure());
      context.fail("this mapper should cause an error");
    } catch (AssertionError e) {
      String s = e.toString();
      if (s.contains("MappingException")) {
        // expected
      } else {
        throw ExceptionUtil.createRuntimeException(e);
      }
    }

  }

  @Test
  public void testNoKeyGenerator(TestContext context) {
    try {
      ResultContainer r = saveRecord(context, new EmbeddedMapper_NoKeyGenerator());
      context.fail("this mapper should cause an error");
    } catch (AssertionError e) {
      String s = e.toString();
      if (s.contains("MappingException") && s.contains("needs a defined KeyGenerator")) {
        // expected
      } else {
        throw ExceptionUtil.createRuntimeException(e);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    EmbeddedMapper_Single mapper = new EmbeddedMapper_Single();
    mapper.simpleMapper = new SimpleMapperEmbedded("testname", "secnd prop");
    return mapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.datatypetests.AbstractDatatypeTest#validateAfterSave(io.vertx.ext.unit.
   * TestContext, java.lang.Object, de.braintags.vertx.jomnigate.testdatastore.ResultContainer)
   */
  @Override
  protected void validateAfterSave(TestContext context, Object record, ResultContainer resultContainer) {
    super.validateAfterSave(context, record, resultContainer);
    EmbeddedMapper_Single loaded = (EmbeddedMapper_Single) record;
    context.assertNotNull(loaded.simpleMapper.id);
    // update the record
    loaded.simpleMapper.name = "modified";
    saveRecord(context, loaded);
  }
}
