/*-
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.geo;

import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;
import de.braintags.vertx.jomnigate.datatypes.geojson.Position;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;

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
  public GeoPoint point = new GeoPoint(new Position(15.5, 13.3));

}
