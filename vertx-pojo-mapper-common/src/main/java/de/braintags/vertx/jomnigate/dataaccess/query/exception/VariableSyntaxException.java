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
package de.braintags.vertx.jomnigate.dataaccess.query.exception;

import de.braintags.vertx.jomnigate.dataaccess.query.IVariableFieldCondition;

/**
 * An exception thrown if a variable field value of a {@link IVariableFieldCondition} can not be parsed
 *
 * @author sschmitt
 *
 */
public class VariableSyntaxException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * @param message
   *          the exception message
   * @param cause
   *          the underlying exception
   */
  public VariableSyntaxException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   *          the exception message
   */
  public VariableSyntaxException(String message) {
    super(message);
  }

}
