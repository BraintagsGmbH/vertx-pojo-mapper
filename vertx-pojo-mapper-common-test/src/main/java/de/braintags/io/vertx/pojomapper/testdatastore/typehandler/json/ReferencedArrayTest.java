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
import de.braintags.io.vertx.pojomapper.testdatastore.ResultContainer;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.SimpleMapper;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.ReferenceMapper_Array;
import io.vertx.ext.unit.TestContext;

/**
 * Tests for testing embedded Arrays
 * 
 * @author Michael Remme
 * 
 */
public class ReferencedArrayTest extends AbstractTypeHandlerTest {

  @SuppressWarnings("rawtypes")
  @Test
  public void extreme(TestContext context) {
    clearTable(context, ReferenceMapper_Array.class.getSimpleName());
    ReferenceMapper_Array record = new ReferenceMapper_Array();
    record.simpleMapper = null;
    saveRecord(context, record);
    IQuery<ReferenceMapper_Array> query = getDataStore(context).createQuery(ReferenceMapper_Array.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    ReferenceMapper_Array loaded = (ReferenceMapper_Array) list.get(0);
    context.assertNull(loaded.simpleMapper);

    record.simpleMapper = new SimpleMapper[0];
    saveRecord(context, record);
    query = getDataStore(context).createQuery(ReferenceMapper_Array.class);
    list = findAll(context, query);
    context.assertEquals(1, list.size());
    loaded = (ReferenceMapper_Array) list.get(0);
    context.assertNotNull(loaded.simpleMapper);
    context.assertEquals(0, loaded.simpleMapper.length);
  }

  /**
   * Save the list of subobject, save, delete one, save again.
   * 
   * @param context
   */
  @SuppressWarnings({ "unused", "rawtypes" })
  @Test
  public void reduceMembers(TestContext context) {
    clearTable(context, ReferenceMapper_Array.class.getSimpleName());
    ReferenceMapper_Array record = new ReferenceMapper_Array(3);
    saveRecord(context, record);
    IQuery<ReferenceMapper_Array> query = getDataStore(context).createQuery(ReferenceMapper_Array.class);

    ResultContainer rc = find(context, query, 1);
    Object result = findFirst(context, query);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    ReferenceMapper_Array loaded = (ReferenceMapper_Array) list.get(0);
    context.assertNotNull(loaded.simpleMapper);
    context.assertEquals(3, loaded.simpleMapper.length);

    SimpleMapper[] old = record.simpleMapper;
    record.simpleMapper = new SimpleMapper[2];
    record.simpleMapper[0] = old[1];
    record.simpleMapper[1] = old[2];

    saveRecord(context, record);
    query = getDataStore(context).createQuery(ReferenceMapper_Array.class);
    list = findAll(context, query);
    context.assertEquals(1, list.size());
    loaded = (ReferenceMapper_Array) list.get(0);
    context.assertNotNull(loaded.simpleMapper);
    context.assertEquals(2, loaded.simpleMapper.length);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    BaseRecord mapper = new ReferenceMapper_Array(3);
    return mapper;
  }

  @Override
  protected String getTestFieldName() {
    return "simpleMapper";
  }

  @Override
  protected String getExpectedTypeHandlerClassName() {
    return "de.braintags.io.vertx.pojomapper.json.typehandler.handler.ArrayTypeHandlerReferenced";
  }

}
