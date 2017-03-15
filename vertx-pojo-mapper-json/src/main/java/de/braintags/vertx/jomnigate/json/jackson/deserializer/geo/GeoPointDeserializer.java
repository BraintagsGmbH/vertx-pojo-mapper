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
package de.braintags.vertx.jomnigate.json.jackson.deserializer.geo;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;
import de.braintags.vertx.jomnigate.datatypes.geojson.Position;
import de.braintags.vertx.jomnigate.json.jackson.deserializer.AbstractDataStoreDeserializer;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class GeoPointDeserializer extends AbstractDataStoreDeserializer<GeoPoint> {

  /**
   * @param datastore
   * @param annotated
   */
  public GeoPointDeserializer(IDataStore datastore, Annotated annotated) {
    super(datastore, annotated);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
   * com.fasterxml.jackson.databind.DeserializationContext)
   */
  @Override
  public GeoPoint deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    Position position = deserializePosition(p, ctxt);
    p.skipChildren();
    return new GeoPoint(position);
  }

  public Position deserializePosition(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    {
      ArrayNode node = (ArrayNode) p.readValueAsTree().get("coordinates");
      return new Position(node.get(0).asDouble(), node.get(1).asDouble());
    }
  }

}
