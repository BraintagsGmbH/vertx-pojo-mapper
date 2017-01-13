/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers;

import de.braintags.io.vertx.pojomapper.datatypes.Price;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

/**
 * 
 * 
 * @author mremme
 * 
 */
public class PriceTypeHandler extends AbstractDecimalTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public PriceTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Price.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.AbstractDecimalTypeHandler#createInstance(java.
   * lang.String)
   */
  @Override
  protected Object createInstance(String value) {
    return new Price(value);
  }

}
