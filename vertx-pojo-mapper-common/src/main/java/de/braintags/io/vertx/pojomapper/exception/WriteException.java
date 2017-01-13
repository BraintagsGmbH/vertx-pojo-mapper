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
/*
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
 * An WriteException is thrown, when during writing a record into the gateway an error occured
 * 
 * @author Michael Remme
 * 
 */

public class WriteException extends RuntimeException {

  /**
   * 
   */
  public WriteException() {
    super();
  }

  /**
   * @param message
   */
  public WriteException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public WriteException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public WriteException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public WriteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
