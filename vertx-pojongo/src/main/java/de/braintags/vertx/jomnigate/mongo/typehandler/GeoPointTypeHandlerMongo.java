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
package de.braintags.vertx.jomnigate.mongo.typehandler;

import de.braintags.vertx.jomnigate.json.typehandler.handler.GeoPointTypeHandlerJson;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;

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
