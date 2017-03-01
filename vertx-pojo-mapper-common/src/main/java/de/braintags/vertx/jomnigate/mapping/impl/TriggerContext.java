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
package de.braintags.vertx.jomnigate.mapping.impl;

import java.util.function.Function;

import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterSave;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeSave;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.ITriggerContext;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * A TriggerContext can be used as argument for mapper methods, which are annotated by one of the annotations like
 * {@link BeforeSave}, {@link AfterSave} etc.
 * 
 * @author Michael Remme
 * 
 */
public class TriggerContext implements ITriggerContext {
  private IMapper mapper;
  private Future<Void> future = Future.future();

  /**
   * 
   */
  TriggerContext(IMapper mapper, Handler<AsyncResult<Void>> handler) {
    this.mapper = mapper;
    setHandler(handler);
  }

  /**
   * Get the instance of IMapper, which is underlaying the current request
   * 
   * @return the mapper
   */
  @Override
  public final IMapper getMapper() {
    return mapper;
  }

  /**
   * @return
   * @see io.vertx.core.Future#isComplete()
   */
  @Override
  public boolean isComplete() {
    return future.isComplete();
  }

  /**
   * @param handler
   * @return
   * @see io.vertx.core.Future#setHandler(io.vertx.core.Handler)
   */
  @Override
  public Future<Void> setHandler(Handler<AsyncResult<Void>> handler) {
    return future.setHandler(handler);
  }

  /**
   * @param result
   * @see io.vertx.core.Future#complete(java.lang.Object)
   */
  @Override
  public void complete(Void result) {
    future.complete(result);
  }

  /**
   * 
   * @see io.vertx.core.Future#complete()
   */
  @Override
  public void complete() {
    future.complete();
  }

  /**
   * @param throwable
   * @see io.vertx.core.Future#fail(java.lang.Throwable)
   */
  @Override
  public void fail(Throwable throwable) {
    future.fail(throwable);
  }

  /**
   * @param failureMessage
   * @see io.vertx.core.Future#fail(java.lang.String)
   */
  @Override
  public void fail(String failureMessage) {
    future.fail(failureMessage);
  }

  /**
   * @return
   * @see io.vertx.core.Future#result()
   */
  @Override
  public Void result() {
    return future.result();
  }

  /**
   * @return
   * @see io.vertx.core.Future#cause()
   */
  @Override
  public Throwable cause() {
    return future.cause();
  }

  /**
   * @return
   * @see io.vertx.core.Future#succeeded()
   */
  @Override
  public boolean succeeded() {
    return future.succeeded();
  }

  /**
   * @return
   * @see io.vertx.core.Future#failed()
   */
  @Override
  public boolean failed() {
    return future.failed();
  }

  /**
   * @param handler
   * @param next
   * @return
   * @see io.vertx.core.Future#compose(io.vertx.core.Handler, io.vertx.core.Future)
   */
  @Override
  public <U> Future<U> compose(Handler<Void> handler, Future<U> next) {
    return future.compose(handler, next);
  }

  /**
   * @param mapper
   * @return
   * @see io.vertx.core.Future#compose(java.util.function.Function)
   */
  @Override
  public <U> Future<U> compose(Function<Void, Future<U>> mapper) {
    return future.compose(mapper);
  }

  /**
   * @param value
   * @return
   * @see io.vertx.core.Future#map(java.lang.Object)
   */
  @Override
  public <V> Future<V> map(V value) {
    return future.map(value);
  }

  /**
   * @return
   * @see io.vertx.core.Future#completer()
   */
  @Override
  public Handler<AsyncResult<Void>> completer() {
    return future.completer();
  }

  /**
   * @param arg0
   * @see io.vertx.core.Future#handle(io.vertx.core.AsyncResult)
   */
  @Override
  public void handle(AsyncResult<Void> arg0) {
    future.handle(arg0);
  }

  /**
   * @param mapper
   * @return
   * @see io.vertx.core.Future#map(java.util.function.Function)
   */
  @Override
  public <U> Future<U> map(Function<Void, U> mapper) {
    return future.map(mapper);
  }

  /**
   * @param mapper
   * @return
   * @see io.vertx.core.Future#otherwise(java.util.function.Function)
   */
  @Override
  public Future<Void> otherwise(Function<Throwable, Void> mapper) {
    return future.otherwise(mapper);
  }

  /**
   * @param value
   * @return
   * @see io.vertx.core.Future#otherwise(java.lang.Object)
   */
  @Override
  public Future<Void> otherwise(Void value) {
    return future.otherwise(value);
  }

  /**
   * @param mapper
   * @return
   * @see io.vertx.core.Future#recover(java.util.function.Function)
   */
  @Override
  public Future<Void> recover(Function<Throwable, Future<Void>> mapper) {
    return future.recover(mapper);
  }

  /**
   * @return
   * @see io.vertx.core.Future#tryComplete()
   */
  @Override
  public boolean tryComplete() {
    return future.tryComplete();
  }

  /**
   * @param arg0
   * @return
   * @see io.vertx.core.Future#tryComplete(java.lang.Object)
   */
  @Override
  public boolean tryComplete(Void arg0) {
    return future.tryComplete(arg0);
  }

  /**
   * @param arg0
   * @return
   * @see io.vertx.core.Future#tryFail(java.lang.String)
   */
  @Override
  public boolean tryFail(String arg0) {
    return future.tryFail(arg0);
  }

  /**
   * @param arg0
   * @return
   * @see io.vertx.core.Future#tryFail(java.lang.Throwable)
   */
  @Override
  public boolean tryFail(Throwable arg0) {
    return future.tryFail(arg0);
  }

}
