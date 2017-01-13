/*-
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
package de.braintags.io.vertx.pojomapper.testdatastore;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.OnlyIdMapper;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TestOnlyIdMapper extends DatastoreBaseTest {

  @Test
  public void testInsert(TestContext context) {
    clearTable(context, "OnlyIdMapper");
    OnlyIdMapper sm = new OnlyIdMapper();
    ResultContainer resultContainer = saveRecord(context, sm);

    // SimpleQuery for all records
    IQuery<OnlyIdMapper> query = getDataStore(context).createQuery(OnlyIdMapper.class);
    resultContainer = find(context, query, 1);
  }

}
