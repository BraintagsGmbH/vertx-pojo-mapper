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
package de.braintags.vertx.jomnigate.observer.impl;

import java.util.ArrayList;
import java.util.List;

import de.braintags.vertx.jomnigate.dataaccess.IAccessResult;
import de.braintags.vertx.jomnigate.dataaccess.IDataAccessObject;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

/**
 * 
 * 
 * @author Michael Remme
 * @param <T>
 *          the type of the IDataAccessObject
 * @param <U>
 *          the type of the IAccessResult
 */
public abstract class AbstractEventHandler<T extends IDataAccessObject<?>, U extends IAccessResult> {

  /**
   * Handles the event {@link ObserverEventType#BEFORE_SAVE }
   * 
   * @param accessObject
   * @param result
   * @param context
   * @param ol
   * @return
   */
  public Future<Void> handle(T accessObject, U result, IObserverContext context, List<IObserver> ol) {
    Future<Void> f = Future.future();
    CompositeFuture cf = loopObserver(ol, accessObject, result, context);
    cf.setHandler(cfr -> {
      if (cfr.failed()) {
        f.fail(cfr.cause());
      } else {
        f.complete();
      }
    });
    return f;
  }

  /**
   * for each defined observer, process the entities of the write object
   * 
   * @param ol
   * @param accessObject
   * @param result
   * @param context
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected CompositeFuture loopObserver(List<IObserver> ol, T accessObject, U result, IObserverContext context) {
    List<Future> fl = new ArrayList<>();
    for (IObserver observer : ol) {
      fl.add(loopEntities(observer, accessObject, result, context));
    }
    return CompositeFuture.all(fl);
  }

  /**
   * Execute the current observer on each entity of the write object
   * 
   * @param observer
   * @param accessObject
   * @param result
   * @param context
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected Future<Void> loopEntities(IObserver observer, T accessObject, U result, IObserverContext context) {
    Future<Void> f = Future.future();
    List<Future> fl = createEntityFutureList(observer, accessObject, context);
    if (fl.isEmpty()) {// if all handlers work fire-and-forget or ifnothing was handled
      f.complete();
    } else {
      CompositeFuture cf = CompositeFuture.all(fl);
      cf.setHandler(res -> {
        if (res.failed()) {
          f.fail(res.cause());
        } else {
          f.complete();
        }
      });
    }
    return f;
  }

  /**
   * Use the entities to create the Future list
   * 
   * @param observer
   * @param writeObject
   * @param context
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected abstract List<Future> createEntityFutureList(IObserver observer, T writeObject, IObserverContext context);
}
