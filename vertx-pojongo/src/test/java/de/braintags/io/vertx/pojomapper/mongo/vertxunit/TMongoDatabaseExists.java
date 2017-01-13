/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mongo.vertxunit;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.testdatastore.DatastoreBaseTest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.TestContext;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class TMongoDatabaseExists extends DatastoreBaseTest {
  private static Logger LOGGER = LoggerFactory.getLogger(TMongoDatabaseExists.class);

  @Test
  public void checkDatabase(TestContext context) {
    MongoDataStore ds = (MongoDataStore) getDataStore(context);
    LOGGER.info("Using database " + ds.getDatabase());
  }

}
