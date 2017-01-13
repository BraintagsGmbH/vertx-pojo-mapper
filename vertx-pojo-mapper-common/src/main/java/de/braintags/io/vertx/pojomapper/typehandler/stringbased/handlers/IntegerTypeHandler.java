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

import java.math.BigInteger;

import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class IntegerTypeHandler extends AbstractNumericTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public IntegerTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, int.class, Integer.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers.AbstractNumericTypeHandler#createInstance(java.
   * lang.String)
   */
  @Override
  protected Object createInstance(String value) {
    return new BigInteger(value).intValue();
  }

}
