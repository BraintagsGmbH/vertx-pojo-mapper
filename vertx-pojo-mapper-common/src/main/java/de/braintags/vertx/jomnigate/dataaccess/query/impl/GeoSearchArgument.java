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
package de.braintags.vertx.jomnigate.dataaccess.query.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.braintags.vertx.jomnigate.datatypes.geojson.GeoJsonObject;

/**
 * GeoSearchArgument contains possible parameters of a GeoSearch
 * 
 * @author Michael Remme
 * 
 */

@JsonSerialize(using = GeoSearchArgumentSerializer.class)
@JsonDeserialize(using = GeoSearchArgumentDeserializer.class)
public class GeoSearchArgument {

  public static final String COORDINATES = "coordinates";

  private final GeoJsonObject geoJson;
  private int distance = -1;

  public GeoSearchArgument(GeoJsonObject geoJson, int distance) {
    this.geoJson = geoJson;
    this.distance = distance;
  }

  /**
   * @return the geoJson
   */
  public GeoJsonObject getGeoJson() {
    return geoJson;
  }

  /**
   * @return the distance
   */
  public int getDistance() {
    return distance;
  }
}
