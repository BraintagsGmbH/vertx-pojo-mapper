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

/**
 * This exception is thrown, when a record shall be inserted with an id, which already exists in the datastore
 * 
 * @author Michael Remme
 * 
 */
public class DuplicateKeyException extends RuntimeException {

  /**
   * 
   */
  public DuplicateKeyException() {
    super();
  }

  /**
   * @param message
   * @param cause
   */
  public DuplicateKeyException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   */
  public DuplicateKeyException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public DuplicateKeyException(Throwable cause) {
    super(cause);
  }

}
