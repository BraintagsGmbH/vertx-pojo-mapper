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
 * Used to store the result of {@link IFieldParameterHandler}
 * 
 * @author Michael Remme
 * 
 */
public interface IFieldParameterResult {

  /**
   * Get the column part of a query, typically the name of the column where to search inside
   * 
   * @return
   */
  String getColName();

  /**
   * Get the operator of a query, something like "=", "<" etc.
   * 
   * @return
   */
  String getOperator();

  /**
   * Get the value, where to search for
   * 
   * @return
   */
  Object getValue();

}
