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

import de.braintags.io.vertx.pojomapper.datastoretest.TestBaseTest;

/**
 * 
 * @author Michael Remme
 * 
 */

@RunWith(Suite.class)
@SuiteClasses({ TestBaseTest.class })
public class AllTestsMySql {
  // -DIDatastoreContainer=de.braintags.io.vertx.pojomapper.mysql.MySqlDataStoreContainer
  /**
   * , TestSimpleInsert.class
   */
  public AllTestsMySql() {
  }

}
