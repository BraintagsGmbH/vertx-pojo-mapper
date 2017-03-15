/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.json.jackson.serializer.geo;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;
import de.braintags.vertx.jomnigate.datatypes.geojson.Position;
import de.braintags.vertx.jomnigate.json.jackson.serializer.AbstractDataStoreSerializer;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class GeoPointSerializer extends AbstractDataStoreSerializer<GeoPoint> {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param datastore
   */
  public GeoPointSerializer(IDataStore datastore) {
    super(datastore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java.lang.Object,
   * com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
   */
  @Override
  public void serialize(GeoPoint value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
    jgen.writeStartObject();
    jgen.writeStringField("type", value.getType().getTypeName());
    writePosition(value.getCoordinates(), jgen);
    jgen.writeEndObject();
  }

  public void writePosition(Position value, JsonGenerator jgen) throws IOException {
    jgen.writeArrayFieldStart("coordinates");
    // jgen.writeStartArray(value.getValues().size());
    for (Double d : value.getValues()) {
      jgen.writeNumber(d);
    }
    jgen.writeEndArray();
  }

}
