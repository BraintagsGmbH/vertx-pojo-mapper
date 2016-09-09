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
package de.braintags.io.vertx.pojomapper.testdatastore;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.testdatastore.mapper.City;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.Country;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.Street;
import io.vertx.ext.unit.TestContext;

/**
 * General tests for embedded entities.
 * 
 * @author Michael Remme
 * 
 */
public class TestEmbeddedEntity extends DatastoreBaseTest {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(TestEmbeddedEntity.class);

  @Test
  public void simpleTest(TestContext context) {
    initCountry(context);

  }

  protected Country initCountry(TestContext context) {
    Country country = new Country();
    country.name = "Germany";
    City city = new City();
    city.name = "Willich";
    country.cities.add(city);
    city.streets.add(new Street("testsrteet"));
    ResultContainer rc = DatastoreBaseTest.saveRecord(context, country);
    Object id = rc.writeResult.iterator().next().getId();
    Country savedCountry = (Country) DatastoreBaseTest.findRecordByID(context, Country.class, country.id);
    context.assertTrue(savedCountry.cities.size() == 1);
    context.assertTrue(savedCountry.cities.get(0).id != null, "ID of subobject not set");
    context.assertTrue(savedCountry.cities.get(0).streets != null);
    context.assertTrue(savedCountry.cities.get(0).streets.size() == 1);
    return savedCountry;
  }

}
