/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.exception;

import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;

/**
 * An exception which occured during a query execution
 * 
 * @author Michael Remme
 * 
 */
public class QueryException extends RuntimeException {

  public QueryException(Throwable cause) {
    super(cause);
  }

  public QueryException(IQueryExpression mq, Throwable cause) {
    super(mq.toString(), cause);
  }

}
