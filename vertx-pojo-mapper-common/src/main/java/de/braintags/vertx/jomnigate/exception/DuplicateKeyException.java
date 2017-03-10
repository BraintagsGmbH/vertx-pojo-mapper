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

/**
 * This exception is thrown when a record can not be inserted because it violates a unique key constraint, mostly (but
 * not always) a duplicate ID
 * 
 * @author Michael Remme
 * 
 */
public class DuplicateKeyException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DuplicateKeyException() {
    super();
  }

  public DuplicateKeyException(String message, Throwable cause) {
    super(message, cause);
  }

  public DuplicateKeyException(String message) {
    super(message);
  }

  public DuplicateKeyException(Throwable cause) {
    super(cause);
  }

}
