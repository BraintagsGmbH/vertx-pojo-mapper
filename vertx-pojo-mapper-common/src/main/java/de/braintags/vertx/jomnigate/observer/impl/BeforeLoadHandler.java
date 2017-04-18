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

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import io.vertx.core.Future;

/**
 * Handles the event {@link ObserverEventType#BEFORE_LOAD }
 * 
 * @author Michael Remme
 * 
 */
public class BeforeLoadHandler extends AbstractEventHandler<IQuery<?>, IQueryResult<?>> {

  @SuppressWarnings("rawtypes")
  @Override
  protected List<Future> createEntityFutureList(IObserver observer, IQuery<?> queryObject, IQueryResult<?> result,
      IObserverContext context) {
    List<Future> fl = new ArrayList<>();
    fl.add(Future.failedFuture(
        new UnsupportedOperationException("we should not land here, cause there are no entities before load")));
    return fl;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.impl.AbstractEventHandler#loopEntities(de.braintags.vertx.jomnigate.observer.
   * IObserver, de.braintags.vertx.jomnigate.dataaccess.IDataAccessObject,
   * de.braintags.vertx.jomnigate.dataaccess.IAccessResult, de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  protected Future<Void> loopEntities(IObserver observer, IQuery<?> accessObject, IQueryResult<?> result,
      IObserverContext context) {
    IObserverEvent event = IObserverEvent.createEvent(ObserverEventType.BEFORE_LOAD, null, null, accessObject,
        accessObject.getDataStore());
    if (observer.canHandleEvent(event, context)) {
      return observer.handleEvent(event, context);
    } else {
      return Future.succeededFuture();
    }
  }

}
