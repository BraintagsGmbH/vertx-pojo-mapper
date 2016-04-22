package de.braintags.io.vertx.pojomapper.mongo.typehandler;

import de.braintags.io.vertx.pojomapper.json.typehandler.handler.GeoPointTypeHandlerJson;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class GeoPointTypeHandlerMongo extends GeoPointTypeHandlerJson {

  /**
   * @param typeHandlerFactory
   */
  public GeoPointTypeHandlerMongo(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory);
  }

}
