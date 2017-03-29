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
package de.braintags.vertx.jomnigate.observer;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * IObserver is the instance, which is executed on a given event
 * 
 * @author Michael Remme
 * 
 */
public interface IObserver {

  /**
   * Checks wether the current instance can handle the given event
   * 
   * @param event
   * @return
   */
  boolean handlesEvent(ObserverEventType event);

  /**
   * Called to execute the given event
   * 
   * @param event
   * @param handler
   */
  void handleEvent(IObserverEvent event, Handler<AsyncResult<Void>> handler);

}
