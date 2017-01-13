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

/**
 * An enumeration which describes the existing types from GeoJSON
 * 
 * @author Michael Remme
 * 
 */
public enum GeoJsonType {

  /**
   * A GeometryCollection
   */
  GEOMETRY_COLLECTION(
      "GeometryCollection"),

  /**
   * A LineString
   */
  LINE_STRING(
      "LineString"),

  /**
   * A MultiLineString
   */
  MULTI_LINE_STRING(
      "MultiLineString"),

  /**
   * A MultiPoint
   */
  MULTI_POINT(
      "MultiPoint"),

  /**
   * A MultiPolygon
   */
  MULTI_POLYGON(
      "MultiPolygon"),

  /**
   * A Point
   */
  POINT(
      "Point"),

  /**
   * A Polygon
   */
  POLYGON(
      "Polygon");

  private final String typeName;

  GeoJsonType(final String typeName) {
    this.typeName = typeName;
  }

  /**
   * Gets the GeoJSON-defined name for the object type.
   *
   * @return the GeoJSON-defined type name
   */
  public String getTypeName() {
    return typeName;
  }

}
