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

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryPart;

/**
 * 
 * 
 * @author sschmitt
 * 
 */
public class UnknownQueryPartException extends QueryExpressionBuildException {

  private static final long serialVersionUID = 1L;

  /**
   * @param message
   */
  public UnknownQueryPartException(IQueryPart queryPart) {
    super("Unknown query part class: " + queryPart.getClass());
  }

}
