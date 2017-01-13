/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.datatypes.geojson;

import static de.braintags.io.vertx.util.assertion.Assert.isTrueArgument;
import static de.braintags.io.vertx.util.assertion.Assert.notNull;

import java.util.Collections;
import java.util.List;

import com.mongodb.client.model.geojson.PolygonCoordinates;

/**
 * A representation of a GeoJSON MultiPolygon.
 * 
 * @author Michael Remme
 * 
 */
public class GeoMultiPolygon extends GeoJsonObject {
  private final List<PolygonCoordinates> coordinates;

  /**
   * Construct an instance with the given coordinates.
   *
   * @param coordinates
   *          the coordinates
   */
  public GeoMultiPolygon(List<PolygonCoordinates> coordinates) {
    notNull("coordinates", coordinates);
    isTrueArgument("coordinates has no null elements", !coordinates.contains(null));
    this.coordinates = Collections.unmodifiableList(coordinates);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoJsonObject#getType()
   */
  @Override
  public GeoJsonType getType() {
    return GeoJsonType.MULTI_POLYGON;
  }

  /**
   * Gets the coordinates.
   *
   * @return the coordinates
   */
  public List<PolygonCoordinates> getCoordinates() {
    return coordinates;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    GeoMultiPolygon that = (GeoMultiPolygon) o;

    if (!coordinates.equals(that.coordinates)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + coordinates.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "MultiPolygon{" + "coordinates=" + coordinates + '}';
  }
}
