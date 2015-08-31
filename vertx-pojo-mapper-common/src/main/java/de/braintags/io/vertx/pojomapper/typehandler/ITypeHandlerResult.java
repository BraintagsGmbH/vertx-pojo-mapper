/*
 * Copyright 2015 Braintags GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * You may elect to redistribute this code under this licenses.
 */

package de.braintags.io.vertx.pojomapper.typehandler;

/**
 * Used to store the result of the work of an {@link ITypeHandler}
 * 
 * @author Michael Remme
 * 
 */
public interface ITypeHandlerResult {

  /**
   * Set the result of the work of an {@link ITypeHandler}
   * 
   * @param result
   *          the result to be set
   */
  void setResult(Object result);

  /**
   * Get the result of the work of an {@link ITypeHandler}
   * 
   * @return the result
   */
  Object getResult();

}
