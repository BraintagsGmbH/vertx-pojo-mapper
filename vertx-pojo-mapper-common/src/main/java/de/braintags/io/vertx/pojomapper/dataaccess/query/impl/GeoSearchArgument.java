/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoJsonObject;

/**
 * GeoSearchArgument contains possible parameters of a GeoSearch
 * 
 * @author Michael Remme
 * 
 */
public class GeoSearchArgument {
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
