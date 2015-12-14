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
 * Exception is thrown, when an expected record wasn't found
 * 
 * @author Michael Remme
 * 
 */
public class NoSuchRecordException extends RuntimeException {

  /**
   * 
   */
  public NoSuchRecordException() {
  }

  /**
   * @param message
   */
  public NoSuchRecordException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public NoSuchRecordException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public NoSuchRecordException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public NoSuchRecordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
