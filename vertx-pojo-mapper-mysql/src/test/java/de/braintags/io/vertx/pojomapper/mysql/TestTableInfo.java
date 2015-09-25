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

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.annotation.field.Property;
import de.braintags.io.vertx.pojomapper.datastoretest.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.MiniMapper;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.StringColumnHandler;
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TestTableInfo extends DatastoreBaseTest {
  private static final Logger log = LoggerFactory.getLogger(TestTableInfo.class);

  /**
   * 
   */
  public TestTableInfo() {
  }

  @Override
  protected VertxOptions getOptions() {
    VertxOptions options = new VertxOptions();
    options.setBlockedThreadCheckInterval(10000);
    options.setWarningExceptionTime(10000);
    return options;
  }

  @Test
  public void simpleTest() {
    log.info("-->>test");
    assertNotNull(datastoreContainer);
    testComplete();
  }

  @Test
  public void testMetaData() {
    IMapper mapper = getDataStore().getMapperFactory().getMapper(MiniMapper.class);
    ITableInfo ti = mapper.getTableInfo();
    assertNotNull(ti);
    assertEquals("MiniMapper", ti.getName());
    List<String> colNames = ti.getColumnNames();
    assertEquals(2, colNames.size());

    checkColumn(ti, "id", "int", Property.UNDEFINED_INTEGER, Property.UNDEFINED_INTEGER, 10, StringColumnHandler.class);
    checkColumn(ti, "name", "varchar", 255, Property.UNDEFINED_INTEGER, Property.UNDEFINED_INTEGER,
        StringColumnHandler.class);
  }

  private void checkColumn(ITableInfo ti, String name, String type, int length, int precision, int scale,
      Class colHandler) {
    IColumnInfo ci = ti.getColumnInfo(name);
    assertNotNull(ci);
    assertEquals(name, ci.getName());
    assertEquals(length, ci.getLength());
    assertEquals(type, ci.getType());
    assertEquals(precision, ci.getPrecision());
    assertEquals(scale, ci.getScale());
    assertEquals(colHandler, ci.getColumnHandler().getClass());
  }

}
