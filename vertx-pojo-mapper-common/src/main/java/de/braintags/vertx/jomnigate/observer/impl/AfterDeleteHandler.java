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

import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDeleteResult;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import io.vertx.core.Future;

/**
 * Handles the event {@link ObserverEventType#AFTER_DELETE }
 * 
 * @author Michael Remme
 * 
 */
public class AfterDeleteHandler extends AbstractEventHandler<IDelete<?>, IDeleteResult> {

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.impl.AbstractEventHandler#createEntityFutureList(de.braintags.vertx.jomnigate
   * .observer.IObserver, de.braintags.vertx.jomnigate.dataaccess.IDataAccessObject,
   * de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  protected List<Future> createEntityFutureList(IObserver observer, IDelete<?> queryObject, IObserverContext context) {
    List<Future> fl = new ArrayList<>();

    fl.add(Future.failedFuture(new UnsupportedOperationException()));
    return fl;
  }

}
