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

import static com.mongodb.assertions.Assertions.notNull;

/**
 * A representation of a GeoJSON Point.
 * 
 * @author Michael Remme
 * 
 */
public class GeoPoint extends GeoJsonObject {

  private Position coordinate;

  public GeoPoint() {
    // empty
  }

  /**
   * Construct an instance with the given coordinate.
   *
   * @param coordinate
   *          the non-null coordinate of the point
   */
  public GeoPoint(final Position coordinate) {
    this.coordinate = notNull("coordinates", coordinate);
  }

  @Override
  public GeoJsonType getType() {
    return GeoJsonType.POINT;
  }

  /**
   * Gets the GeoJSON coordinates of this point.
   *
   * @return the coordinates
   */
  public Position getCoordinates() {
    return coordinate;
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

    GeoPoint point = (GeoPoint) o;

    if (!coordinate.equals(point.coordinate)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    return 31 * result + coordinate.hashCode();
  }

  @Override
  public String toString() {
    return "Point{" + "coordinate=" + coordinate + '}';
  }

}
