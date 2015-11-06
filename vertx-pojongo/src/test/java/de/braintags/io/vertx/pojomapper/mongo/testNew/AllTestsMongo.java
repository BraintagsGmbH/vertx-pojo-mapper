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
package de.braintags.io.vertx.pojomapper.mongo.testNew;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.io.vertx.pojomapper.AllTestsPojoJson;
import de.braintags.io.vertx.pojomapper.datastoretest.AllTestsCommon;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

@RunWith(Suite.class)
@SuiteClasses({ AllTestsPojoJson.class, TestMongoMapper.class, TestMongoQueryRambler.class, AllTestsCommon.class })
public class AllTestsMongo {
  // -DIDatastoreContainer=de.braintags.io.vertx.pojomapper.mongo.testNew.MongoDataStoreContainer
  // -DBlockedThreadCheckInterval=1000 -DWarningExceptionTime=1000
  // -Ddb_name=unitTestDb

  public AllTestsMongo() {
  }

}
