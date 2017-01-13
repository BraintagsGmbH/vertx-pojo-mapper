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

import java.util.Collections;
import java.util.List;

import de.braintags.io.vertx.util.assertion.Assert;

/**
 * A representation of a GeoJSON MultiPoint.
 * 
 * @author Michael Remme
 * 
 */
public class GeoMultiPoint extends GeoJsonObject {
  private final List<Position> coordinates;

  /**
   * Construct an instance with the given coordinates.
   * 
   * @param coordinates
   *          the coordinates of the new instance
   */
  public GeoMultiPoint(final List<Position> coordinates) {
    Assert.notNull("coordinates", coordinates);
    Assert.isTrueArgument("coordinates contains only non-null positions", !coordinates.contains(null));
    this.coordinates = Collections.unmodifiableList(coordinates);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoJsonObject#getType()
   */
  @Override
  public GeoJsonType getType() {
    return GeoJsonType.MULTI_POINT;
  }

  /**
   * Gets the GeoJSON coordinates of this MultiPoint.
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

    GeoMultiPoint multiPoint = (GeoMultiPoint) o;

    if (!coordinates.equals(multiPoint.coordinates)) {
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
    return "MultiPoint{" + "coordinates=" + coordinates + '}';
  }
}
