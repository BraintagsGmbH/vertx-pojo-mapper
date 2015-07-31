/*
 *
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.exception;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

@SuppressWarnings("serial")
public class QueryParameterException extends RuntimeException {

  /**
   * 
   */
  public QueryParameterException() {
  }

  /**
   * @param message
   */
  public QueryParameterException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public QueryParameterException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public QueryParameterException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public QueryParameterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
