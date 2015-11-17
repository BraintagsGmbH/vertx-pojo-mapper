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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.io.vertx.pojomapper.AllTestsPojoJson;
import de.braintags.io.vertx.pojomapper.testdatastore.AllTestsCommon;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

@RunWith(Suite.class)
@SuiteClasses({ AllTestsPojoJson.class, TestMongoMapper.class, TestMongoQueryRambler.class, AllTestsCommon.class })
public class AllTestsMongoVertxUnit {
  // -DIDatastoreContainer=de.braintags.io.vertx.pojomapper.mongo.vertxunit.MongoDataStoreContainer
  // -DBlockedThreadCheckInterval=10000000 -DWarningExceptionTime=10000000
  // -Ddb_name=PojongoTestDatabase
  // -DstartMongoLocal=true
  // -DtestTimeout=5
  public AllTestsMongoVertxUnit() {
  }

}
