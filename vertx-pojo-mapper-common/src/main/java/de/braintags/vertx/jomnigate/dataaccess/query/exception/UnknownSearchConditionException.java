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
package de.braintags.vertx.jomnigate.dataaccess.query.exception;

import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;

/**
 * Thrown if a query expression encouters an unknown implementation of {@link ISearchCondition} that it can not handle
 * 
 * @author sschmitt
 * 
 */
public class UnknownSearchConditionException extends QueryExpressionBuildException {

  private static final long serialVersionUID = 1L;

  /**
   * @param message
   */
  public UnknownSearchConditionException(ISearchCondition searchCondition) {
    super("Unknown ISearchCondition implementation: " + searchCondition.getClass());
  }

}
