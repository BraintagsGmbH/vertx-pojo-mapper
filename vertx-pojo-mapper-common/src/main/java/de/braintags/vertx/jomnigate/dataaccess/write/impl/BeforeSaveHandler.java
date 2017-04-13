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
package de.braintags.vertx.jomnigate.dataaccess.write.impl;

import java.util.ArrayList;
import java.util.List;

import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

/**
 * Handles the event {@link ObserverEventType#BEFORE_SAVE }
 * 
 * @author Michael Remme
 * 
 */
public class BeforeSaveHandler {

  /**
   * @param write
   * @param storeObject
   * @param context
   * @param ol
   * @return
   */
  public <T> Future<Void> handle(IWrite<T> write, IStoreObject<T, ?> storeObject, IObserverContext context,
      List<IObserver> ol) {
    Future<Void> f = Future.future();
    CompositeFuture cf = loopObserver(ol, write, storeObject, context);
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
   * for each defined observer, process the instance
   * 
   * @param ol
   * @param write
   * @param storeObject
   * @param context
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected <T> CompositeFuture loopObserver(List<IObserver> ol, IWrite<T> writeObject, IStoreObject<T, ?> storeObject,
      IObserverContext context) {
    List<Future> fl = new ArrayList<>();
    for (IObserver observer : ol) {
      IObserverEvent event = IObserverEvent.createEvent(ObserverEventType.BEFORE_SAVE, storeObject, null, writeObject,
          writeObject.getDataStore());
      if (observer.canHandleEvent(event, context)) {
        Future tf = observer.handleEvent(event, context);
        if (tf != null) {
          fl.add(tf);
        }
      }
    }
    return CompositeFuture.all(fl);
  }

}
