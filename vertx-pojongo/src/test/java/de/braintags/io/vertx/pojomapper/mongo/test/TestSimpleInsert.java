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
package de.braintags.io.vertx.pojomapper.mongo.test;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.mongo.test.mapper.MiniMapper;

public class TestSimpleInsert extends MongoBaseTest {
  private static Logger logger = LoggerFactory.getLogger(TestSimpleInsert.class);

  @SuppressWarnings("unused")
  private static final Logger log = LoggerFactory.getLogger(TestSimpleInsert.class);

  private static final int LOOP = 5000;

  @BeforeClass
  public static void beforeClass() throws Exception {
    System.setProperty("connection_string", "mongodb://localhost:27017");
    System.setProperty("db_name", "PojongoTestDatabase");
    MongoBaseTest.startMongo();
  }

  @AfterClass
  public static void afterClass() {
    MongoBaseTest.stopMongo();
  }

  @Test
  public void testInsert() {
    MiniMapper sm = new MiniMapper();
    ResultContainer resultContainer = saveRecord(sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    List<MiniMapper> mapperList = new ArrayList<MiniMapper>();
    for (int i = 0; i < LOOP; i++) {
      mapperList.add(new MiniMapper("looper"));
    }
    resultContainer = saveRecords(mapperList);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    if (LOOP != resultContainer.writeResult.size()) {
      // check wether records weren't written or "only" IWriteResult is incomplete
      IQuery<MiniMapper> query = getDataStore().createQuery(MiniMapper.class);
      query.field("name").is("looper");
      find(query, LOOP);
      assertEquals(LOOP, resultContainer.writeResult.size());
    }

    IQuery<MiniMapper> query = getDataStore().createQuery(MiniMapper.class);
    query.field("name").is("looper");
    ResultContainer reCo = find(query, LOOP);
    if (reCo.assertionError != null)
      throw reCo.assertionError;

    CountDownLatch latch = new CountDownLatch(1);

    IDelete<MiniMapper> delete = getDataStore().createDelete(MiniMapper.class);
    reCo.queryResult.toArray(toArray -> {
      if (toArray.failed()) {
        logger.error("", toArray.cause());
        fail(toArray.cause().toString());
        latch.countDown();
      } else {
        Object[] obs = toArray.result();
        try {
          assertEquals(LOOP, obs.length);
        } catch (AssertionError e) {
          latch.countDown();
          logger.error("", e);
          throw e;
        }
        for (Object ob : obs) {
          delete.add((MiniMapper) ob);
        }
        ResultContainer reCod = delete(delete, query, 0);
        if (reCod.assertionError != null) {
          logger.error("", reCod.assertionError);
          fail(reCod.assertionError.toString());
          latch.countDown();
        } else {
          logger.info(reCod.deleteResult.getOriginalCommand());
          latch.countDown();
        }

      }
    });

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }
}
