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
package de.braintags.vertx.jomnigate.testdatastore;

import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.testdatastore.mapper.GeoMapper2;
import io.vertx.ext.unit.TestContext;

/**
 * Test indexing of fields
 * 
 * 
 * @author Michael Remme
 *
 */
public class TestIndex extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestIndex.class);

  @Test
  public void testGeoIndex(TestContext context) {
    clearTable(context, GeoMapper2.class.getSimpleName());
    IQuery<GeoMapper2> q = getDataStore(context).createQuery(GeoMapper2.class);
    findAll(context, q);
    checkIndex(context, q.getMapper(), "testindex");
  }

  @BeforeClass
  public static void beforeClass(TestContext context) {
    dropTable(context, GeoMapper2.class.getSimpleName());
  }
}
