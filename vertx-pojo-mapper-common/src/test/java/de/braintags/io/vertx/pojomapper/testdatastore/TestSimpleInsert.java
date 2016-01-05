/*
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.testdatastore;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.MiniMapper;
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

public class TestSimpleInsert extends DatastoreBaseTest {
  private static Logger logger = LoggerFactory.getLogger(TestSimpleInsert.class);
  private static final int LOOP = 500;

  @Test
  public void testRoundtrip(TestContext context) {
    Async async = context.async();
    clearTable(context, "MiniMapper");

    MiniMapper sm = new MiniMapper();
    ResultContainer resultContainer = saveRecord(context, sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    List<MiniMapper> mapperList = new ArrayList<MiniMapper>();
    for (int i = 0; i < LOOP; i++) {
      mapperList.add(new MiniMapper("looper"));
    }
    resultContainer = saveRecords(context, mapperList, 0);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    if (LOOP != resultContainer.writeResult.size()) {
      // check wether records weren't written or "only" IWriteResult is incomplete
      IQuery<MiniMapper> query = getDataStore().createQuery(MiniMapper.class);
      query.field("name").is("looper");
      find(context, query, LOOP);
      context.assertEquals(LOOP, resultContainer.writeResult.size());
    }

    IQuery<MiniMapper> query = getDataStore().createQuery(MiniMapper.class);
    query.field("name").is("looper");
    ResultContainer reCo = find(context, query, LOOP);
    if (reCo.assertionError != null)
      throw reCo.assertionError;

    IDelete<MiniMapper> delete = getDataStore().createDelete(MiniMapper.class);
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
        ResultContainer reCod = delete(context, delete, query, 0);
        if (reCod.assertionError != null) {
          logger.error("", reCod.assertionError);
          context.fail(reCod.assertionError.toString());
          async.complete();
        } else {
          logger.info(reCod.deleteResult.getOriginalCommand());
          async.complete();
        }

      }
    });

  }
}
