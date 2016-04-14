package de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.geo;

import de.braintags.io.vertx.pojomapper.datatypes.geojson.Point;
import de.braintags.io.vertx.pojomapper.datatypes.geojson.Position;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;

/**
 * <br>
 * <br>
 * Copyright: Copyright (c) 14.04.2016 <br>
 * Company: Braintags GmbH <br>
 * 
 * @author mremme
 * 
 */

public class GeoPointRecord extends BaseRecord {
  public Point point = new Point(new Position(15.5, 13.3));

}
