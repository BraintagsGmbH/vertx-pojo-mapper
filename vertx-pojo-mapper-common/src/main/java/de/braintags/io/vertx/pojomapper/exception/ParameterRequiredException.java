/*
 * Copyright 2015 Braintags GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * You may elect to redistribute this code under this licenses.
 */

package de.braintags.io.vertx.pojomapper.exception;

/**
 * The exception signales that an expected parameter was not found
 * 
 * @author Michael Remme
 * 
 */

public class ParameterRequiredException extends RuntimeException {

  /**
   * 
   */
  public ParameterRequiredException() {
  }

  /**
   * @param message
   */
  public ParameterRequiredException(String parameterName) {
    super("Missing parameter: " + parameterName);
  }

  /**
   * @param cause
   */
  public ParameterRequiredException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public ParameterRequiredException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public ParameterRequiredException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
