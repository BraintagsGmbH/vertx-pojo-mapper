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

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;
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
    IQuery<Person> query = dataStore.createQuery(Person.class);
    query.setSearchCondition(query.and(query.isEqual("name", "peter"), query.in("secName", "eins", "zwei"),
        query.isEqual("weight", 15)));
    logger.info("--- start Rambler");

    final IQuery<Person> exQuery = query;
    exQuery.buildQueryExpression(result -> {
      if (result.failed()) {
        logger.error("Error building expression", result.cause());
      } else {
        IQueryExpression expression = result.result();
        logger.info(expression.toString());
      }
    });

  }
}
