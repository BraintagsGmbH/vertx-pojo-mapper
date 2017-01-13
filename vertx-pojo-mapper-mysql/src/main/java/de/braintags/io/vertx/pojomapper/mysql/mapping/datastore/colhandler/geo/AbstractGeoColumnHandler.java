/*-
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
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
