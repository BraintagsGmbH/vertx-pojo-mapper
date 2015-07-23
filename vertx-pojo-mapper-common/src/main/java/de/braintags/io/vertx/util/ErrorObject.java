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

package de.braintags.io.vertx.util;

import io.vertx.core.Future;

/**
 * A simple instance to transfer error information in an event queue
 * 
 * @author Michael Remme
 * 
 */

public class ErrorObject {
  private boolean error = false;
  private Throwable throwable;

  /**
   * 
   */
  public ErrorObject() {
  }

  /**
   * did an error occur?
   * 
   * @return the error
   */
  public final boolean isError() {
    return error;
  }

  /**
   * did an error occur?
   * 
   * @param error
   *          the error to set
   */
  public final void setError(boolean error) {
    this.error = error;
  }

  /**
   * an optional information about Throwable
   * 
   * @return the throwable
   */
  public final Throwable getThrowable() {
    return throwable;
  }

  /**
   * Return an occured Throwable as {@link RuntimeException}
   * 
   * @return the exception
   */
  public RuntimeException getRuntimeException() {
    return throwable instanceof RuntimeException ? (RuntimeException) throwable : new RuntimeException(throwable);
  }

  /**
   * an optional information about Throwable
   * 
   * @param throwable
   *          the throwable to set
   */
  public final void setThrowable(Throwable throwable) {
    this.throwable = throwable;
  }

  /**
   * Set the error flag to true and add the Throwable
   * 
   * @param throwable
   *          the Throwable
   */
  public void setError(Throwable throwable) {
    this.error = true;
    this.throwable = throwable;
  }

  /**
   * Creates a failed future with the contained Throwable
   * 
   * @return the {@link Future}
   */
  public Future<?> toFuture() {
    if (throwable == null)
      throw new NullPointerException("Throwable is null");
    return Future.failedFuture(throwable);
  }
}
