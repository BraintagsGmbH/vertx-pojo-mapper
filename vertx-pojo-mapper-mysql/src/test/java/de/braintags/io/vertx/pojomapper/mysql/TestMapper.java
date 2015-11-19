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
package de.braintags.io.vertx.pojomapper.mysql;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.StringColumnHandler;
import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.testdatastore.TestHelper;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.MiniMapper;
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

  @Test
  public void simpleTest(TestContext context) {
    log.info("-->>test");
    context.assertNotNull(TestHelper.getDatastoreContainer());
  }

  @Test
  public void testId(TestContext context) {
    IMapper mapper = getDataStore().getMapperFactory().getMapper(MiniMapper.class);
    IField idField = mapper.getField("id");
    context.assertNotNull(idField, "Improve that the name of the id field is 'id'");

    Id ann = (Id) idField.getAnnotation(Id.class);
    if (ann == null)
      Assert.fail("Annotation Id must not be null");

    IField field = mapper.getIdField();
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
    } catch (MappingException e) {
      // this is the expected result
    }

  }

  @Test
  public void testMetaData(TestContext context) {
    IMapper mapper = getDataStore().getMapperFactory().getMapper(MiniMapper.class);
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
