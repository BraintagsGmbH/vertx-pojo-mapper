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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.vertx.jomnigate.testdatastore.AllTestsCommon;
import de.braintags.vertx.jomnigate.testdatastore.TestHelper;
import de.braintags.vertx.jomnigate.testdatastore.typehandler.TypeHandlerTestSuite;

/**
 * 
 * @author Michael Remme
 * 
 */

@RunWith(Suite.class)
@SuiteClasses({ TSqlMapperFactory.class, TestSqlExpressions.class, TQuery.class, TReflection.class, TestMapper.class,
    TypeHandlerTestSuite.class, AllTestsCommon.class })
public class TestAllMySql {

  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestAllMySql.class);

  @BeforeClass
  public static void startup() throws Exception {
    LOGGER.info("STARTING SUITE");
    TestMapper.supportsColumnHandler = true;
  }

  @AfterClass
  public static void shutdown() throws Exception {
    LOGGER.info("STOPPING SUITE");
    TestHelper.shutdown();
  }

}
