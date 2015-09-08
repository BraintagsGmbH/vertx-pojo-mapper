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
package de.braintags.io.vertx.pojomapper.test;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.mongo.test.MongoBaseTest;
import de.braintags.io.vertx.pojomapper.mongo.test.ResultContainer;
import de.braintags.io.vertx.pojomapper.mongo.test.TestOnlyIdMapper;
import de.braintags.io.vertx.pojomapper.test.mapper.OnlyIdMapper;

/**
 * Just to test that a mapper with only the id field is working
 *
 * @author Michael Remme
 * 
 */

public class TestOnlyIdMapper extends MongoBaseTest {
  private static Logger logger = LoggerFactory.getLogger(TestOnlyIdMapper.class);

  /**
   * 
   */
  public TestOnlyIdMapper() {
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    System.setProperty("connection_string", "mongodb://localhost:27017");
    System.setProperty("db_name", "PojongoTestDatabase");
    MongoBaseTest.startMongo();
  }

  @AfterClass
  public static void afterClass() {
    MongoBaseTest.stopMongo();
  }

  @Test
  public void testInsert() {
    OnlyIdMapper sm = new OnlyIdMapper();
    ResultContainer resultContainer = saveRecord(sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<OnlyIdMapper> query = getDataStore().createQuery(OnlyIdMapper.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

  }

}
