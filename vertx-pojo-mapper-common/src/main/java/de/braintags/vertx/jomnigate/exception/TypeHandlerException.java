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
package de.braintags.vertx.jomnigate.exception;

import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;

/**
 * Exception is thrown when an error in the use of {@link ITypeHandler} was detected
 * 
 * @author Michael Remme
 * 
 */

@SuppressWarnings("serial")
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
