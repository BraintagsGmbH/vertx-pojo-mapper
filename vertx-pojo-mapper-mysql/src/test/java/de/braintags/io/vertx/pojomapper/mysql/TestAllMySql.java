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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.io.vertx.pojomapper.AllTestsPojoJson;
import de.braintags.io.vertx.pojomapper.TMapperFactory;
import de.braintags.io.vertx.pojomapper.testdatastore.AllTestsCommon;
import de.braintags.io.vertx.pojomapper.testdatastore.TestHelper;

/**
 * 
 * @author Michael Remme
 * 
 */

@RunWith(Suite.class)
@SuiteClasses({ AllTestsPojoJson.class, TestMassInsert.class, TestMapper.class, TestSqlQueryRambler.class,
    AllTestsCommon.class })
public class TestAllMySql {
  /*
   * -DIDatastoreContainer=de.braintags.io.vertx.pojomapper.mysql.MySqlDataStoreContainer
   * -DMySqlDataStoreContainer.username=root -DMySqlDataStoreContainer.password=qdmaha3t
   * -Djava.util.logging.config.file=src/main/resources/logging.properties
   * -DBlockedThreadCheckInterval=100000 -DWarningExceptionTime=100000 -DtestTimeout=200
   * 
   */

  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestAllMySql.class);

  @BeforeClass
  public static void startup() throws Exception {
    LOGGER.info("STARTING SUITE");
    TMapperFactory.supportsColumnHandler = true;
  }

  @AfterClass
  public static void shutdown() throws Exception {
    LOGGER.info("STOPPING SUITE");
    TestHelper.shutdown();
  }

}
