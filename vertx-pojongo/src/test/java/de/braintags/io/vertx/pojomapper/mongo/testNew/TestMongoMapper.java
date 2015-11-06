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
package de.braintags.io.vertx.pojomapper.mongo.testNew;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.datastoretest.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.MiniMapper;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class TestMongoMapper extends DatastoreBaseTest {
  private static Logger log = LoggerFactory.getLogger(TestMongoMapper.class);

  /**
   * 
   */
  public TestMongoMapper() {
  }

  @Test
  public void simpleTest() {
    log.info("-->>test");
    assertNotNull(datastoreContainer);
    testComplete();
  }

  @Test
  public void testId() {
    log.info("-->> testId");
    IMapper mapper = getDataStore().getMapperFactory().getMapper(MiniMapper.class);
    IField idField = mapper.getField("id");
    assertNotNull("Improve that the name of the id field is 'id'", idField);

    Id ann = (Id) idField.getAnnotation(Id.class);
    if (ann == null)
      Assert.fail("Annotation Id must not be null");

    IField field = mapper.getIdField();
    assertNotNull(field);
    Assert.assertSame(field, idField);

    String javaName = field.getName();
    IColumnInfo ci = field.getMapper().getTableInfo().getColumnInfo(field);
    assertNotNull(ci);
    String dbName = ci.getName();

    Assert.assertNotEquals(javaName, dbName);

    try {
      field = mapper.getField("doesntexist");
      fail("this should throw an exception here");
    } catch (MappingException e) {
      // this is the expected result
    }

  }

  @Test
  public void testMetaData() {
    log.info("-->> testMetaData");
    IMapper mapper = getDataStore().getMapperFactory().getMapper(MiniMapper.class);
    ITableInfo ti = mapper.getTableInfo();
    assertNotNull(ti);
    assertEquals("MiniMapper", ti.getName());
    List<String> colNames = ti.getColumnNames();
    assertEquals(2, colNames.size());
  }

}
