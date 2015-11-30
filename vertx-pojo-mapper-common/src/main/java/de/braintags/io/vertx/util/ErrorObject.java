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
 * @param <E>
 *          the underlaying class, which shall be delivered to the Handler as {@link AsyncResult}
 * 
 */
public class ErrorObject<E> {
  private static final Logger logger = LoggerFactory.getLogger(ErrorObject.class);
  private Throwable throwable;
  private boolean errorHandled = false;
  private Handler<AsyncResult<E>> handler;

  /**
   * Creates an instance with a handler, which will be informed, when an Exception is added
   * into the current instance
   * 
   * @handler the handler to be informed about errors
   */
  public ErrorObject(Handler<AsyncResult<E>> handler) {
    this.handler = handler;
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
    handleError();
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
  boolean handleError() {
    if (handler == null)
      return false;
    if (isError()) {
      if (errorHandled)
        return true;
      handler.handle(toFuture());
      errorHandled = true;
      return true;
    }
    return false;
  }

  /**
   * If the method {@link #handleError(Handler)} was called, this returns true
   * 
   * @return the errorHandled
   */
  public final boolean isErrorHandled() {
    return errorHandled;
  }

  /**
   * The internal handler to be used for information
   * 
   * @return
   */
  protected Handler<AsyncResult<E>> getHandler() {
    return handler;
  }
}
