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
package de.braintags.io.vertx.pojomapper.datastoretest.typehandler;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.datastoretest.DatastoreBaseTest;
import de.braintags.io.vertx.pojomapper.datastoretest.ResultContainer;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler.BaseRecord;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractTypeHandlerTest extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractTypeHandlerTest.class);

  @Test
  public void testSaveAndReadRecord() {
    BaseRecord record = createInstance();
    dropTable(record.getClass().getSimpleName());
    ResultContainer resultContainer = saveRecord(record);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<? extends BaseRecord> query = getDataStore().createQuery(record.getClass());
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        result.cause().printStackTrace();
      } else {
        assertTrue(record.equals(result.result()));
        LOGGER.info("finished!");
      }
    });

  }

  /**
   * Create the instance of
   * 
   * @return
   */
  public abstract BaseRecord createInstance();
}
