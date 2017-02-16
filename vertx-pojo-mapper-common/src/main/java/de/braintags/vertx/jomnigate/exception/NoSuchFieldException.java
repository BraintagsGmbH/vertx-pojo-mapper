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

import de.braintags.vertx.jomnigate.mapping.IMapper;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class NoSuchFieldException extends RuntimeException {

  /**
   * Comment for <code>FIELD_S_DOES_NOT_EXIST</code>
   */
  private static final String FIELD_DOES_NOT_EXIST = "Field '%s' does not exist in %s";

  /**
   * 
   */
  public NoSuchFieldException() {
  }

  /**
   * @param message
   */
  public NoSuchFieldException(String fieldName) {
    super("Field does not exist: " + fieldName);
  }

  /**
   * @param message
   */
  public NoSuchFieldException(IMapper mapper, String fieldName) {
    super(String.format(FIELD_DOES_NOT_EXIST, fieldName, mapper.getMapperClass().getName()));
  }

  /**
   * @param cause
   */
  public NoSuchFieldException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public NoSuchFieldException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public NoSuchFieldException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
