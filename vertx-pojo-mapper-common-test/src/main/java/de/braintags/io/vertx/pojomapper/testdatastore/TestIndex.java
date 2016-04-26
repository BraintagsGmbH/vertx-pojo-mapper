/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.testdatastore;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.GeoMapper;
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
    clearTable(context, GeoMapper.class.getSimpleName());
    IQuery<GeoMapper> q = getDataStore(context).createQuery(GeoMapper.class);
    findAll(context, q);
    checkIndex(context, q.getMapper(), "testindex");
  }

}
