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
package de.braintags.vertx.jomnigate.dataaccess.datatypetests.geo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.braintags.vertx.jomnigate.dataaccess.datatypetests.AbstractDatatypeTest;
import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;
import de.braintags.vertx.jomnigate.datatypes.geojson.Position;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.geo.GeoPointRecord;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class GeoPointTest extends AbstractDatatypeTest {

  public GeoPointTest() {
    super("point");
  }

  @Test
  public void testPosition(TestContext context) {
    try {
      Position pos = new Position((List<Double>) null);
      context.fail(new AssertionError("expected exception here"));
    } catch (Exception e) {
      // expected
    }

    try {
      Position pos = new Position((Iterator) null);
      context.fail(new AssertionError("expected exception here"));
    } catch (Exception e) {
      // expected
    }

    try {
      List<Double> list = new ArrayList<>();
      Position pos = new Position(list);
      context.fail(new AssertionError("expected exception here"));
    } catch (Exception e) {
      // expected
    }

    try {
      List<Double> list = new ArrayList<>();
      Position pos = new Position(list.iterator());
      context.fail(new AssertionError("expected exception here"));
    } catch (Exception e) {
      // expected
    }

    try {
      List<Double> list = new ArrayList<>();
      list.add(5.3);
      Position pos = new Position(list);
      context.fail(new AssertionError("expected exception here"));
    } catch (Exception e) {
      // expected
    }

    try {
      List<Double> list = new ArrayList<>();
      list.add(5.3);
      Position pos = new Position(list.iterator());
      context.fail(new AssertionError("expected exception here"));
    } catch (Exception e) {
      // expected
    }

    try {
      List<Double> list = new ArrayList<>();
      list.add(5.3);
      list.add(7.3);
      Position pos = new Position(list);
    } catch (Exception e) {
      context.fail(e);
    }

    try {
      List<Double> list = new ArrayList<>();
      list.add(5.3);
      list.add(7.3);
      Position pos = new Position(list.iterator());
    } catch (Exception e) {
      context.fail(e);
    }

  }

  @Test
  public void testValues(TestContext context) {
    GeoPoint point = new GeoPoint(new Position(15.5, 13.3));
    context.assertFalse(point.equals(new Object()));
    context.assertTrue(point.equals(point));

    GeoPoint point2 = new GeoPoint(new Position(15.5, 13.4));
    context.assertFalse(point.equals(point2));

    GeoPoint point3 = new GeoPoint(new Position(15.5, 13.4));
    context.assertFalse(point.equals(point2));

    context.assertEquals(point2.toString(), point3.toString());
    context.assertEquals(point2.hashCode(), point3.hashCode());

    context.assertNotEquals(point.toString(), point3.toString());
    context.assertNotEquals(point.hashCode(), point3.hashCode());

    try {
      point = new GeoPoint(new Position(270, 13.3));
      context.fail(new AssertionError("expected exception here"));
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      point = new GeoPoint(new Position(15.5, 100));
      context.fail(new AssertionError("expected exception here"));
    } catch (IllegalArgumentException e) {
      // expected
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    return new GeoPointRecord();
  }

}
