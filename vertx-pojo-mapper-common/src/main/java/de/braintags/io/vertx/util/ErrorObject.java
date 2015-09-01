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
package de.braintags.io.vertx.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * A simple instance to transfer error information in an event queue
 * 
 * @author Michael Remme
 * 
 */

public class ErrorObject<E> {
  private static Logger logger = LoggerFactory.getLogger(ErrorObject.class);
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
    return throwable != null;
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
    logger.info("", throwable);
  }

  /**
   * Creates a failed future with the contained Throwable
   * 
   * @return the {@link Future}
   */
  public Future<E> toFuture() {
    if (throwable == null)
      throw new NullPointerException("Throwable is null");
    return Future.failedFuture(throwable);
  }

  /**
   * If an error occured or a result exists, the handler will be called with a succeedded or error {@link Future}
   * 
   * @param handler
   */
  public boolean handleError(Handler<AsyncResult<E>> handler) {
    if (isError()) {
      handler.handle(toFuture());
      return true;
    }
    return false;
  }

}
