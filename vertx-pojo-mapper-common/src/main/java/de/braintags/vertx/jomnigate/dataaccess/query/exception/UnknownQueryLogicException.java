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

import de.braintags.vertx.jomnigate.dataaccess.query.QueryLogic;

/**
 * Thrown if the query expression encounters an unknown {@link QueryLogic}
 * 
 * @author sschmitt
 * 
 */
public class UnknownQueryLogicException extends QueryExpressionBuildException {

  private static final long serialVersionUID = -6759031631610910054L;

  public UnknownQueryLogicException(QueryLogic queryLogic) {
    super("Unknown query logic: " + queryLogic);
  }

}
