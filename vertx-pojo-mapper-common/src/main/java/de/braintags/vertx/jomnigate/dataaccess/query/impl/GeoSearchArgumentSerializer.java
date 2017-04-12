/*-
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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;

/**
 * Custom jackson serializer for {@link GeoSearchArgument}
 * 
 * @author sschmitt
 * 
 */
public class GeoSearchArgumentSerializer extends StdSerializer<GeoSearchArgument> {
  private static final long serialVersionUID = 1L;

  private static final String COORDINATES = "coordinates";

  protected GeoSearchArgumentSerializer() {
    super(GeoSearchArgument.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java.lang.Object,
   * com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
   */
  @Override
  public void serialize(GeoSearchArgument value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    GeoPoint geoPoint = (GeoPoint) value.getGeoJson();

    gen.writeStartObject();
    gen.writeFieldName("$geometry");

    gen.writeStartObject();
    gen.writeFieldName("type");
    gen.writeString(geoPoint.getType().getTypeName());
    gen.writeFieldName(COORDINATES);

    gen.writeStartArray(geoPoint.getCoordinates().getValues().size());
    for (Double coordinate : geoPoint.getCoordinates().getValues()) {
      gen.writeNumber(coordinate);
    }
    gen.writeEndArray();

    gen.writeEndObject();

    if (value.getDistance() >= 0) {
      gen.writeFieldName("$maxDistance");
      gen.writeNumber(value.getDistance());
    }
    gen.writeEndObject();
  }

}
