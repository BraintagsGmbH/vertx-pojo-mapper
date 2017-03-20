/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.testdatastore;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniNumberMapper;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TestMassInsert extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestMassInsert.class);
  private static final int LOOP = 500;

  @Test
  public void simpleTest(TestContext context) {
    clearTable(context, "MiniNumberMapper");

    List<MiniNumberMapper> mapperList = new ArrayList<>();
    for (int i = 0; i < LOOP; i++) {
      mapperList.add(new MiniNumberMapper("looper " + i, i));
    }
    ResultContainer resultContainer = saveRecords(context, mapperList, 0);

    IWriteResult wr = resultContainer.writeResult;
    if (LOOP != wr.size()) {
      LOGGER.warn("size of writeresult is incorrect - checking records in datastore");
      // check wether records weren't written or "only" IWriteResult is incomplete
      IQuery<MiniNumberMapper> query = getDataStore(context).createQuery(MiniNumberMapper.class);
      query.setSearchCondition(ISearchCondition.isEqual(MiniNumberMapper.NAME, "looper"));
      find(context, query, LOOP);
      context.assertEquals(LOOP, resultContainer.writeResult.size());
      context.fail("The write result hasn't got the right content, the records in the datastore are correct");
    }

  }

}
