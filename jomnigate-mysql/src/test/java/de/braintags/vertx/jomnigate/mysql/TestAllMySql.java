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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.braintags.vertx.jomnigate.AllTestsPojoJson;
import de.braintags.vertx.jomnigate.testdatastore.AllTestsCommon;

/**
 * 
 * @author Michael Remme
 * 
 */

@RunWith(Suite.class)
@SuiteClasses({ AllTestsPojoJson.class, AllTestsCommon.class })
public class TestAllMySql {
  /*
   * 
   * -DIDatastoreContainer=de.braintags.vertx.jomnigate.mysql.MySqlDataStoreContainer
   * -DMySqlDataStoreContainer.host=192.168.42.180 -DMySqlDataStoreContainer.username=xxxxx
   * -DMySqlDataStoreContainer.password=xxxxxx -DBlockedThreadCheckInterval=100000
   * -DWarningExceptionTime=100000
   * -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.Log4jLogDelegateFactory
   * 
   */

}
