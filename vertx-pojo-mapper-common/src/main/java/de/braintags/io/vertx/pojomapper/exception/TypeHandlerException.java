/*
 * Copyright 2014 Red Hat, Inc.
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

import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

/**
 * Exception is thrown when an error in the use of {@link ITypeHandler} was detected
 * 
 * @author Michael Remme
 * 
 */

public class TypeHandlerException extends RuntimeException {

  /**
   * 
   */
  public TypeHandlerException() {
  }

  /**
   * @param message
   */
  public TypeHandlerException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public TypeHandlerException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public TypeHandlerException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public TypeHandlerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
