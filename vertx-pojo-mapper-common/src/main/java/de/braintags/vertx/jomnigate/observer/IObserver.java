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

import io.vertx.core.Future;

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
   * @param context
   * @return true, if event shall be handled, false otherwise
   */
  boolean handlesEvent(IObserverEvent event, IObserverContext context);

  /**
   * Called to execute the given event. The content of the IObserverEvent depends on the event type, which is handled.
   * See appropriate method descriptions in {@link IObserverEvent} for further information
   * 
   * @param event
   *          the event to be handled
   * @param context
   *          the context can be used to store context data during processing of the underlaying complete action
   * @return the observer can return a valid {@link Future}, where for the surrounding process will wait; or it can
   *         return NULL, which will process the action as fire-and-forget
   */
  Future<Void> handleEvent(IObserverEvent event, IObserverContext context);

}
