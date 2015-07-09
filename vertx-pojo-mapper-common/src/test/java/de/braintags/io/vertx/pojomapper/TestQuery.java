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

package de.braintags.io.vertx.pojomapper;

import org.junit.Before;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.Query;
import de.braintags.io.vertx.pojomapper.impl.DummyDataStore;
import de.braintags.io.vertx.pojomapper.mapper.Person;

public class TestQuery {
  private static IDataStore dataStore = new DummyDataStore();

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void test() {
    Query<Person> query = (Query) dataStore.createQuery(Person.class);
    query.field("name").contains("peter").and("weight").is(15).parent();

  }
}
