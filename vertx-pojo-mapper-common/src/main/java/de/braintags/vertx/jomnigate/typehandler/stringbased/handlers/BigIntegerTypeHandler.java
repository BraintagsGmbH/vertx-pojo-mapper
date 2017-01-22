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
package de.braintags.vertx.jomnigate.typehandler.stringbased.handlers;

import java.math.BigInteger;

import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class BigIntegerTypeHandler extends AbstractNumericTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public BigIntegerTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, BigInteger.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.typehandler.stringbased.handlers.AbstractNumericTypeHandler#createInstance(java.
   * lang.String)
   */
  @Override
  protected Object createInstance(String value) {
    return new BigInteger(value);
  }

}
