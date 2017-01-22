/*-
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

package de.braintags.vertx.jomnigate.exception;

/**
 * An InsertException is thrown, when during insert of a record into the gateway an error occured
 * 
 * @author Michael Remme
 * 
 */

public class InsertException extends RuntimeException {

  /**
   * 
   */
  public InsertException() {
    super();
  }

  /**
   * @param message
   */
  public InsertException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public InsertException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public InsertException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public InsertException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
