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
package de.braintags.vertx.jomnigate.datatypes.geojson;

import static de.braintags.vertx.util.assertion.Assert.isTrueArgument;
import static de.braintags.vertx.util.assertion.Assert.notNull;

import java.util.Collections;
import java.util.List;

/**
 * A representation of a GeoJSON LineString.
 * 
 * @author Michael Remme
 * 
 */
public class GeoLineString extends GeoJsonObject {
  private final List<Position> coordinates;

  /**
   * Construct an instance with the given coordinates.
   *
   * @param coordinates
   *          the coordinates
   */
  public GeoLineString(final List<Position> coordinates) {
    notNull("coordinates", coordinates);
    isTrueArgument("coordinates must contain at least two positions", coordinates.size() >= 2);
    isTrueArgument("coordinates contains only non-null positions", !coordinates.contains(null));
    this.coordinates = Collections.unmodifiableList(coordinates);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.datatypes.geojson.GeoJsonObject#getType()
   */
  @Override
  public GeoJsonType getType() {
    return GeoJsonType.LINE_STRING;
  }

  /**
   * Gets the GeoJSON coordinates of this LineString.
   *
   * @return the coordinates
   */
  public List<Position> getCoordinates() {
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

    GeoLineString lineString = (GeoLineString) o;

    if (!coordinates.equals(lineString.coordinates)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    return 31 * result + coordinates.hashCode();
  }

  @Override
  public String toString() {
    return "LineString{" + "coordinates=" + coordinates + '}';
  }
}
