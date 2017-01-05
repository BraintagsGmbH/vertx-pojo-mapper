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

import static de.braintags.io.vertx.pojomapper.dataaccess.query.impl.FieldCondition.in;
import static de.braintags.io.vertx.pojomapper.dataaccess.query.impl.FieldCondition.isEqual;
import static de.braintags.io.vertx.pojomapper.dataaccess.query.impl.QueryAnd.and;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

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
    query.setRootQueryPart(
        and(isEqual("name", "peter"), in("secName", Arrays.asList("eins", "zwei")), isEqual("weight", 15)));
    logger.info("--- start Rambler");

    final Query<Person> exQuery = query;
    // TODO

  }
}
