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
package de.braintags.io.vertx.pojomapper.mongo.vertxunit;

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
 * 
 * @author Michael Remme
 * 
 */

@RunWith(Suite.class)
@SuiteClasses({ AllTestsPojoJson.class, TMongoDatabaseExists.class, TMongoDirect.class, TMongoDirectMassInsert.class,
    TMongoMapper.class, AllTestsCommon.class })
public class TestAllMongo {
  // -DIDatastoreContainer=de.braintags.io.vertx.pojomapper.mongo.vertxunit.MongoDataStoreContainer
  // -DBlockedThreadCheckInterval=10000000 -DWarningExceptionTime=10000000
  // -Ddb_name=PojongoTestDatabase
  // -DstartMongoLocal=true
  // -DtestTimeout=5
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestAllMongo.class);

  public TestAllMongo() {
  }

  @BeforeClass
  public static void startup() throws Exception {
    LOGGER.info("STARTING SUITE");
  }

  @AfterClass
  public static void shutdown() throws Exception {
    LOGGER.info("STOPPING SUITE");
    TestHelper.shutdown();
  }

}
