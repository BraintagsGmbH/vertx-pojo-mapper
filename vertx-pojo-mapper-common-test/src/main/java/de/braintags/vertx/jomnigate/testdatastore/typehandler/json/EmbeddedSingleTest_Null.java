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

import java.util.List;

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.EmbeddedMapper_Single;
import io.vertx.ext.unit.TestContext;

/**
 * Tests for testing embedded Single instance, but instance is null
 * 
 * @author Michael Remme
 * 
 */
public class EmbeddedSingleTest_Null extends AbstractTypeHandlerTest {

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

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    BaseRecord mapper = new EmbeddedMapper_Single();
    return mapper;
  }

  @Override
  protected String getTestFieldName() {
    return "simpleMapper";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.vertx.jomnigate.json.typehandler.handler.ObjectTypeHandlerEmbedded";
  }

}
