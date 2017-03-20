/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate;

import org.junit.Before;
import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression;
import de.braintags.vertx.jomnigate.impl.DummyDataStore;
import de.braintags.vertx.jomnigate.mapper.Person;
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
    query.setSearchCondition(ISearchCondition.and(ISearchCondition.isEqual(Person.NAME, "peter"),
        ISearchCondition.in(Person.SEC_NAME, "eins", "zwei"), ISearchCondition.isEqual(Person.WEIGHT, 15)));
    logger.info("--- start Rambler");

    final IQuery<Person> exQuery = query;
    exQuery.buildQueryExpression(null, result -> {
      if (result.failed()) {
        logger.error("Error building expression", result.cause());
      } else {
        IQueryExpression expression = result.result();
        logger.info(expression.toString());
      }
    });

  }
}
