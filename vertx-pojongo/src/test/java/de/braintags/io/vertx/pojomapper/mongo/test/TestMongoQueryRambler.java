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

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.LogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.Query;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.MongoQueryRambler;
import de.braintags.io.vertx.pojomapper.mongo.test.mapper.Person;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class TestMongoQueryRambler extends MongoBaseTest {
  private static Logger logger = LoggerFactory.getLogger(TestMongoQueryRambler.class);

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
  public void testRambler() {
    IDataStore store = getDataStore();
    Query<Person> query = (Query<Person>) store.createQuery(Person.class);
    IFieldParameter<Query<Person>> fp = query.field("name");
    query = fp.is("peter").field("secName").in(Arrays.asList("eins", "zwei"));
    IFieldParameter<LogicContainer<Query<Person>>> fplc = query.and("weight");
    LogicContainer<Query<Person>> lc = fplc.is(15);
    query = lc.parent();
    logger.info("--- start Rambler");
    MongoQueryRambler rambler = new MongoQueryRambler();

    final Query<Person> exQuery = query;
    exQuery.executeQueryRambler(rambler, result -> {
      if (result.failed()) {

      } else {
        logger.info("--- stop Rambler");

        for (Object arg : exQuery.getChildren()) {
          if (arg instanceof IFieldParameter) {
            IFieldParameter<?> fm = (IFieldParameter<?>) arg;
            logger.info(fm.getValue());
          }
        }

        logger.info(rambler.getJsonObject());
      }
    });

  }

}
