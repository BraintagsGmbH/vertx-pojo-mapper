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
package de.braintags.io.vertx.util;

/**
 * Utility methods dealing with Exceptions
 * 
 * @author Michael Remme
 * 
 */
public class ExceptionUtil {

  /**
   * This method creates a RuntimeException from the given Throwable or casts the Throwable as RuntimeException
   * 
   * @param e
   *          the {@link Throwable} to be examined
   */
  public static RuntimeException createRuntimeException(Throwable e) {
    if (e instanceof RuntimeException)
      return (RuntimeException) e;
    if (e instanceof Error)
      throw (Error) e;
    return new RuntimeException(e);
  }

}