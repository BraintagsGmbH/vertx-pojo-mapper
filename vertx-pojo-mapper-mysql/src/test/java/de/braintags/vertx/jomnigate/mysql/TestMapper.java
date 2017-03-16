/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mysql;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.annotation.field.Property;
import de.braintags.vertx.jomnigate.exception.NoSuchFieldException;
import de.braintags.vertx.jomnigate.mapper.Person;
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo;
import de.braintags.vertx.jomnigate.mapping.datastore.ITableInfo;
import de.braintags.vertx.jomnigate.mysql.mapping.datastore.colhandler.StringColumnHandler;
import de.braintags.vertx.jomnigate.testdatastore.DatastoreBaseTest;
import de.braintags.vertx.jomnigate.testdatastore.TestHelper;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapper;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.TestContext;

/**
 * Testing mapped {@link IMapper} and {@link ITableInfo}
 * 
 * @author Michael Remme
 * 
 */

public class TestMapper extends DatastoreBaseTest {
  private static final Logger log = LoggerFactory.getLogger(TestMapper.class);
  public static boolean supportsColumnHandler = false;

  @Test
  public void testColumnHandler(TestContext context) {
    IMapper mapperDef = getDataStore(context).getMapperFactory().getMapper(Person.class);
    IColumnInfo ci = mapperDef.getTableInfo().getColumnInfo(mapperDef.getField("weight"));
    assertNotNull(ci);
    if (supportsColumnHandler) {
      assertNotNull("No columnhandler found", ci.getColumnHandler());
    } else {
      assertNull(ci.getColumnHandler());
    }
  }

  @Test
  public void simpleTest(TestContext context) {
    log.info("-->>test");
    context.assertNotNull(TestHelper.getDatastoreContainer(context));
  }

  @Test
  public void testId(TestContext context) {
    IMapper mapper = getDataStore(context).getMapperFactory().getMapper(MiniMapper.class);
    IField idField = mapper.getField("id");
    context.assertNotNull(idField, "Improve that the name of the id field is 'id'");

    Id ann = (Id) idField.getAnnotation(Id.class);
    if (ann == null)
      Assert.fail("Annotation Id must not be null");

    IField field = mapper.getIdField().getField();
    context.assertNotNull(field);
    Assert.assertSame(field, idField);

    String javaName = field.getName();
    IColumnInfo ci = field.getMapper().getTableInfo().getColumnInfo(field);
    context.assertNotNull(ci);
    String dbName = ci.getName();
    context.assertEquals(javaName, dbName);

    try {
      field = mapper.getField("doesntexist");
      context.fail("this should throw an exception here");
    } catch (NoSuchFieldException e) {
      // this is the expected result
    }

  }

  @Test
  public void testMetaData(TestContext context) {
    IMapper mapper = getDataStore(context).getMapperFactory().getMapper(MiniMapper.class);
    ITableInfo ti = mapper.getTableInfo();
    context.assertNotNull(ti);
    context.assertEquals("MiniMapper", ti.getName());
    List<String> colNames = ti.getColumnNames();
    context.assertEquals(2, colNames.size());

    checkColumn(context, ti, "id", "int", Property.UNDEFINED_INTEGER, Property.UNDEFINED_INTEGER, 10,
        StringColumnHandler.class);
    checkColumn(context, ti, "name", "varchar", 255, Property.UNDEFINED_INTEGER, Property.UNDEFINED_INTEGER,
        StringColumnHandler.class);
  }

  private void checkColumn(TestContext context, ITableInfo ti, String name, String type, int length, int precision,
      int scale, Class colHandler) {
    IColumnInfo ci = ti.getColumnInfo(name);
    context.assertNotNull(ci);
    context.assertEquals(name, ci.getName());
    context.assertEquals(length, ci.getLength());
    context.assertEquals(type, ci.getType());
    context.assertEquals(precision, ci.getPrecision());
    context.assertEquals(scale, ci.getScale());
    context.assertEquals(colHandler, ci.getColumnHandler().getClass());
  }

}
