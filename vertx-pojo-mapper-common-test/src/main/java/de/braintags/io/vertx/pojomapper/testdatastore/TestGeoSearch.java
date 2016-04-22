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

import java.util.ArrayList;
import java.util.List;

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

  @Test
  public void testKeyGenerator(TestContext context) {
    clearTable(context, GeoMapper.class.getSimpleName());
    createDemoRecords(context);
    IQuery<GeoMapper> query = getDataStore(context).createQuery(GeoMapper.class);
    query.field("position").near(13.408, 52.518, 10);
    List<GeoMapper> found = (List<GeoMapper>) findAll(context, query);
    context.assertEquals(found.size(), 1, "wrong number of records");
  }

  private void createDemoRecords(TestContext context) {
    List<GeoMapper> list = new ArrayList<>();
    list.add(createPoint(6.81, 51.235, "Berlin"));
    list.add(createPoint(13.408, 52.518, "Düsseldorf"));
    list.add(createPoint(6.959, 50.943, "Köln"));
    saveRecords(context, list);
    // getDataStore(context).
  }

  private GeoMapper createPoint(double x, double y, String name) {
    GeoPoint p = new GeoPoint(new Position(x, y));
    return new GeoMapper(p, name);
  }

  // db.GeoMapper.find( "position" : { "$geoNear" : { $geometry : {"type": "POINT", "coordinates":[6.959,50.943]}"} } }
  // )

}
