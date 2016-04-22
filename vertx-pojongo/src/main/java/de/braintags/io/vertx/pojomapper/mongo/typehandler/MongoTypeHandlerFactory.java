package de.braintags.io.vertx.pojomapper.mongo.typehandler;

import de.braintags.io.vertx.pojomapper.json.typehandler.JsonTypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.GeoPointTypeHandlerJson;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MongoTypeHandlerFactory extends JsonTypeHandlerFactory {

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.json.typehandler.JsonTypeHandlerFactory#init()
   */
  @Override
  protected void init() {
    super.init();
    remove(GeoPointTypeHandlerJson.class);
    add(new GeoPointTypeHandlerMongo(this));
  }

}
