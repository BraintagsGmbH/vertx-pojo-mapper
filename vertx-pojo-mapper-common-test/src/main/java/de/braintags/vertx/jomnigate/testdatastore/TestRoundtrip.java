/*-
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

import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.testdatastore.mapper.MiniMapper;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class TestRoundtrip extends DatastoreBaseTest {
  private static Logger logger = LoggerFactory.getLogger(TestRoundtrip.class);
  private static final int LOOP = 50;

  @Test
  public void testRoundtrip(TestContext context) {
    Async async = context.async();
    clearTable(context, "MiniMapper");

    MiniMapper sm = new MiniMapper();
    ResultContainer resultContainer = saveRecord(context, sm);

    List<MiniMapper> mapperList = new ArrayList<>();
    for (int i = 0; i < LOOP; i++) {
      mapperList.add(new MiniMapper("looper"));
    }
    resultContainer = saveRecords(context, mapperList, 0);

    if (LOOP != resultContainer.writeResult.size()) {
      // check wether records weren't written or "only" IWriteResult is incomplete
      IQuery<MiniMapper> query = getDataStore(context).createQuery(MiniMapper.class);
      query.setSearchCondition(query.isEqual("name", "looper"));
      find(context, query, LOOP);
      context.assertEquals(LOOP, resultContainer.writeResult.size());
    }

    IQuery<MiniMapper> query = getDataStore(context).createQuery(MiniMapper.class);
    query.setSearchCondition(query.isEqual("name", "looper"));
    ResultContainer reCo = find(context, query, LOOP);

    IDelete<MiniMapper> delete = getDataStore(context).createDelete(MiniMapper.class);
    reCo.queryResult.toArray(toArray -> {
      if (toArray.failed()) {
        logger.error("", toArray.cause());
        context.fail(toArray.cause().toString());
        async.complete();
      } else {
        Object[] obs = toArray.result();
        try {
          context.assertEquals(LOOP, obs.length);
        } catch (AssertionError e) {
          async.complete();
          logger.error("", e);
          throw e;
        }
        for (Object ob : obs) {
          delete.add((MiniMapper) ob);
        }
        try {
          ResultContainer reCod = delete(context, delete, query, 0);
          logger.info(reCod.deleteResult.getOriginalCommand());
          async.complete();
        } catch (Throwable e) {
          logger.error(delete, e);
          context.fail(e.toString());
          async.complete();
        }

      }
    });

  }
}
