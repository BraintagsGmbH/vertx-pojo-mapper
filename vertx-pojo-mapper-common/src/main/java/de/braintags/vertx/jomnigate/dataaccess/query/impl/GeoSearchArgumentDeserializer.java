package de.braintags.vertx.jomnigate.dataaccess.query.impl;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;

/**
 * Custom jackson deserializer for {@link GeoSearchArgument}
 * 
 * @author sschmitt
 * 
 */
public class GeoSearchArgumentDeserializer extends StdDeserializer<GeoSearchArgument> {
  private static final long serialVersionUID = 1L;

  protected GeoSearchArgumentDeserializer() {
    super(GeoSearchArgument.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
   * com.fasterxml.jackson.databind.DeserializationContext)
   */
  @Override
  public GeoSearchArgument deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    TreeNode node = p.getCodec().readTree(p);

    int distance = 0;
    TreeNode maxDistanceNode = node.get("$maxDistance");
    if (maxDistanceNode != null) {
      distance = ((ValueNode) maxDistanceNode).asInt();
    }

    GeoPoint geoPoint = new GeoPoint();
    TreeNode geometryNode = node.get("$geometry");
    ArrayNode coordinates = ((ArrayNode) geometryNode.get(GeoSearchArgument.COORDINATES));
    for (JsonNode coordinateNode : coordinates) {
      geoPoint.getCoordinates().getValues().add(((ValueNode) coordinateNode).asDouble());
    }

    return new GeoSearchArgument(geoPoint, distance);
  }

}