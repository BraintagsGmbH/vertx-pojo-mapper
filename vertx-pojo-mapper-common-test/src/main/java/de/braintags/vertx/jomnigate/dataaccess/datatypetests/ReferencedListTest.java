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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.ReferenceMapper_List;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.ReferenceMapper_List_WithJsonCreator;
import io.vertx.ext.unit.TestContext;

/**
 * Mapper for testing boolean values
 * 
 * @author Michael Remme
 * 
 */
public class ReferencedListTest extends AbstractDatatypeTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(ReferencedListTest.class);

  public ReferencedListTest() {
    super("simpleMapper");
  }

  @Test
  public void testReferencedWithJsonCreator(TestContext context) {
    clearTable(context, ReferenceMapper_List_WithJsonCreator.class.getSimpleName());
    ReferenceMapper_List_WithJsonCreator record = ReferenceMapper_List_WithJsonCreator.createReferenceMapper_List(1);
    saveRecord(context, record);
    IQuery<ReferenceMapper_List_WithJsonCreator> query = getDataStore(context)
        .createQuery(ReferenceMapper_List_WithJsonCreator.class);
    try {
      List list = findAll(context, query);
      context.assertEquals(1, list.size());
      ReferenceMapper_List_WithJsonCreator loaded = (ReferenceMapper_List_WithJsonCreator) list.get(0);
      context.assertNotNull(loaded.simpleMapper);
      context.assertEquals(1, loaded.simpleMapper.size());
      context.fail("Expected JsonMappingException");
    } catch (Exception e) {
      context.assertTrue(e.toString().contains("JsonMappingException"), "Expected JsonMappingException but was " + e);
    }
  }

  @Test
  public void oneChildInList(TestContext context) {
    clearTable(context, ReferenceMapper_List.class.getSimpleName());
    ReferenceMapper_List record = new ReferenceMapper_List(1);
    saveRecord(context, record);
    IQuery<ReferenceMapper_List> query = getDataStore(context).createQuery(ReferenceMapper_List.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    ReferenceMapper_List loaded = (ReferenceMapper_List) list.get(0);
    context.assertNotNull(loaded.simpleMapper);
    context.assertEquals(1, loaded.simpleMapper.size());
  }

  @Test
  public void extreme2(TestContext context) {
    clearTable(context, ReferenceMapper_List.class.getSimpleName());
    ReferenceMapper_List record = new ReferenceMapper_List();
    record.simpleMapper = new ArrayList<>();
    saveRecord(context, record);
    IQuery<ReferenceMapper_List> query = getDataStore(context).createQuery(ReferenceMapper_List.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    ReferenceMapper_List loaded = (ReferenceMapper_List) list.get(0);
    context.assertNull(loaded.simpleMapper);
  }

  @Test
  public void extreme(TestContext context) {
    clearTable(context, ReferenceMapper_List.class.getSimpleName());
    ReferenceMapper_List record = new ReferenceMapper_List();
    record.simpleMapper = null;
    saveRecord(context, record);
    IQuery<ReferenceMapper_List> query = getDataStore(context).createQuery(ReferenceMapper_List.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    ReferenceMapper_List loaded = (ReferenceMapper_List) list.get(0);
    context.assertNull(loaded.simpleMapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    BaseRecord mapper = new ReferenceMapper_List(5);
    return mapper;
  }

}
