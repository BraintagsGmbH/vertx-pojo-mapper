package de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.geo;

import de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoPoint;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler.AbstractSqlColumnHandler;

/**
 * Abstract implementation for handling GeoJson objects like {@link GeoPoint}
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractGeoColumnHandler extends AbstractSqlColumnHandler {

  /**
   * @param classesToDeal
   */
  public AbstractGeoColumnHandler(Class<?>... classesToDeal) {
    super(classesToDeal);
  }

}
