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
package de.braintags.io.vertx.pojomapper.dataaccess.query.exception;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;

/**
 * Thrown if there is an exception during the conversion process of the general {@link IQuery} to a more native {@link IQueryExpression}
 * 
 * @author sschmitt
 * 
 */
public abstract class QueryExpressionBuildException extends Exception {

  private static final long serialVersionUID = -4972417569758843244L;

  /**
   * @param message
   */
  public QueryExpressionBuildException(String message) {
    super(message);
  }

}
