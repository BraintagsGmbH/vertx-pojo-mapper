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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.io.vertx.pojomapper.AllTestsPojoJson;
import de.braintags.io.vertx.pojomapper.datastoretest.TestBaseTest;
import de.braintags.io.vertx.pojomapper.datastoretest.TestOnlyIdMapper;
import de.braintags.io.vertx.pojomapper.datastoretest.TestSimpleInsert;
import de.braintags.io.vertx.pojomapper.datastoretest.TestSimpleMapper;
import de.braintags.io.vertx.pojomapper.datastoretest.typehandler.TypeHandlerTestSuite;

/**
 * 
 * @author Michael Remme
 * 
 */

@RunWith(Suite.class)
@SuiteClasses({ AllTestsPojoJson.class, TestMapper.class, TestSqlQueryRambler.class, TestBaseTest.class,
    TestSimpleInsert.class, TestSimpleMapper.class, TestOnlyIdMapper.class, TypeHandlerTestSuite.class })
public class AllTestsMySql {
  // -DIDatastoreContainer=de.braintags.io.vertx.pojomapper.mysql.MySqlDataStoreContainer

  /**
   * 
   */
  public AllTestsMySql() {
  }

}
