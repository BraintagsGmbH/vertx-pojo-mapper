/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler;

import de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoJsonObject;

/**
 * ColumnHandler dealing with instances of {@link GeoJsonObject}
 * 
 * @author Michael Remme
 * 
 */
public class GeoJsonColumnHandler extends JsonColumnHandler {

  /**
   * 
   */
  public GeoJsonColumnHandler() {
    super(GeoJsonObject.class);
  }

}
