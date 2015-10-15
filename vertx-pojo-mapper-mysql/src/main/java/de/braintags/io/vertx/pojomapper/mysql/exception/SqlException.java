/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mysql.exception;

import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlQueryRambler;

/**
 * Exception is thrown when an error occured during sql execution
 * 
 * @author Michael Remme
 * 
 */

public class SqlException extends RuntimeException {

  /**
   * 
   */
  public SqlException() {
  }

  /**
   * @param message
   */
  public SqlException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public SqlException(Throwable cause) {
    super(cause);
  }

  /**
   * 
   * @param rambler
   *          an instance of {@link SqlQueryRambler}
   * @param cause
   *          the original exception
   */
  public SqlException(SqlQueryRambler rambler, Throwable cause) {
    super("Error in handling query or delete: " + rambler.toString(), cause);
  }

  /**
   * @param message
   * @param cause
   */
  public SqlException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public SqlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
