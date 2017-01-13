/*-
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
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
