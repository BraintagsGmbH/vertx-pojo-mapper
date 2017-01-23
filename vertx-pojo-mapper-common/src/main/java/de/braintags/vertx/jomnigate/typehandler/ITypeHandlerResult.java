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
package de.braintags.vertx.jomnigate.typehandler;

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
