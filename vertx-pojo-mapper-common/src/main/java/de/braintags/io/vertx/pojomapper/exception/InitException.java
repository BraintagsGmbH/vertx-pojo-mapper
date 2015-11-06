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
package de.braintags.io.vertx.pojomapper.exception;

/**
 * Exception is thrown when initialization fails
 * 
 * @author Michael Remme
 * 
 */
public class InitException extends RuntimeException {

  /**
   * 
   */
  public InitException() {
  }

  /**
   * @param message
   */
  public InitException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public InitException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public InitException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public InitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
