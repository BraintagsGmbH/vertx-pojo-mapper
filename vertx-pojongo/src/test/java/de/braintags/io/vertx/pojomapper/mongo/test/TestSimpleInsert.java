/*
 * Copyright 2014 Red Hat, Inc.
 * 
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

package de.braintags.io.vertx.pojomapper.mongo.test;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;

public class TestSimpleInsert extends MongoBaseTest {

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
    find(query, LOOP);
  }

  class MiniMapper {
    @Id
    public String id = null;
    public String name = "testName";

    MiniMapper() {

    }

    MiniMapper(String name) {
      this.name = name;
    }

  }
}
