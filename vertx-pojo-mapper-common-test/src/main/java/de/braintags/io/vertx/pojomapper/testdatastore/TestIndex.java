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

import de.braintags.io.vertx.pojomapper.testdatastore.mapper.SimpleIndexMapper;
import io.vertx.ext.unit.TestContext;

/**
 * Test indexing of fields
 * 
 * 
 * @author Michael Remme
 *
 */
public class TestIndex extends DatastoreBaseTest {

  @Test
  public void testSimpleMapper(TestContext context) {
    clearTable(context, SimpleIndexMapper.class.getSimpleName());
    SimpleIndexMapper sm = new SimpleIndexMapper();
    sm.name = "testName";
    sm.setSecondProperty("my second property");
    ResultContainer resultContainer = saveRecord(context, sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

}
