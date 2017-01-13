/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.testdatastore;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoPoint;
import de.braintags.io.vertx.pojomapper.datatypes.geojson.Position;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.GeoMapper;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author mremme
 * 
 */
public class TestGeoSearch extends DatastoreBaseTest {
  private double sLong = 6.775763;
  private double sLat = 51.224906;

  @Test
  public void testGeoSearch(TestContext context) {
    clearTable(context, GeoMapper.class.getSimpleName());
    createDemoRecords(context);
    IQuery<GeoMapper> query = getDataStore(context).createQuery(GeoMapper.class);
    query.setSearchCondition(query.near("position", sLong, sLat, 10));
    List<GeoMapper> found = findAll(context, query);
    context.assertEquals(found.size(), 1, "wrong number of records");

    query = getDataStore(context).createQuery(GeoMapper.class);
    query.setSearchCondition(query.near("position", sLong, sLat, 70000));
    found = findAll(context, query);
    context.assertEquals(found.size(), 2, "wrong number of records");

    query = getDataStore(context).createQuery(GeoMapper.class);
    query.setSearchCondition(query.near("position", sLong, sLat, 700000));
    found = findAll(context, query);
    context.assertEquals(found.size(), 3, "wrong number of records");

  }

  public static void createDemoRecords(TestContext context) {
    List<GeoMapper> list = new ArrayList<>();
    list.add(createPoint(13.4111, 52.5236, "Berlin"));
    list.add(createPoint(6.775763, 51.224906, "Düsseldorf"));
    list.add(createPoint(6.95982, 50.940906, "Köln"));
    saveRecords(context, list);
    // getDataStore(context).
  }

  public static GeoMapper createPoint(double x, double y, String name) {
    GeoPoint p = new GeoPoint(new Position(x, y));
    return new GeoMapper(p, name);
  }

  @BeforeClass
  public static void beforeClass(TestContext context) {
    dropTable(context, GeoMapper.class.getSimpleName());
  }
}
