/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.LoggerQueryRamber;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.LogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.Query;
import de.braintags.io.vertx.pojomapper.impl.DummyDataStore;
import de.braintags.io.vertx.pojomapper.mapper.Person;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class TQuery {
  private static Logger logger = LoggerFactory.getLogger(TQuery.class);
  private static IDataStore dataStore = new DummyDataStore();

  @Before
  public void setUp() throws Exception {
    LoggerFactory.initialise();
  }

  @Test
  public void test() {
    Query<Person> query = (Query<Person>) dataStore.createQuery(Person.class);
    IFieldParameter<Query<Person>> fp = query.field("name");
    query = fp.is("peter").field("secName").in(Arrays.asList("eins", "zwei"));
    IFieldParameter<LogicContainer<Query<Person>>> fplc = query.and("weight");
    LogicContainer<Query<Person>> lc = fplc.is(15);
    query = lc.parent();
    logger.info("--- start Rambler");
    LoggerQueryRamber rambler = new LoggerQueryRamber();

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
      }
    });

  }
}
