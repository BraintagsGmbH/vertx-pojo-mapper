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

package de.braintags.io.vertx.pojomapper.mysql;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.io.vertx.pojomapper.AllTestsPojoJson;
import de.braintags.io.vertx.pojomapper.testdatastore.AllTestsCommon;
import de.braintags.io.vertx.pojomapper.testdatastore.TestHelper;

/**
 * 
 * @author Michael Remme
 * 
 */

@RunWith(Suite.class)
@SuiteClasses({ AllTestsPojoJson.class, TestMapper.class, AllTestsCommon.class })
public class TestAllMySql {
  /*
   * 
   * -DIDatastoreContainer=de.braintags.io.vertx.pojomapper.mysql.MySqlDataStoreContainer
   * -DMySqlDataStoreContainer.host=192.168.42.180 -DMySqlDataStoreContainer.username=root
   * -DMySqlDataStoreContainer.password=xt1729x -DBlockedThreadCheckInterval=100000
   * -DWarningExceptionTime=100000
   * -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.Log4jLogDelegateFactory
   * 
   */

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
