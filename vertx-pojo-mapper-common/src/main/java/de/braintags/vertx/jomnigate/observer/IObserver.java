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

import java.util.Properties;

import de.braintags.vertx.jomnigate.annotation.Observer;
import de.braintags.vertx.jomnigate.init.ObserverDefinition;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * IObserver is the instance, which is executed on a given event
 * 
 * @author Michael Remme
 * 
 */
public interface IObserver {

  /**
   * The properties, which can adjust the behaviour of an observer. Those properties can be defined by
   * {@link ObserverDefinition} or by annotation {@link Observer}
   * 
   * @return
   */
  Properties getObserverProperties();

  /**
   * This method is called directly after the properties are set and can be used to validate and prepare some
   * information
   * 
   * @param vertx
   */
  void init(Vertx vertx);

  /**
   * Checks wether the current instance can handle the given event. NOTE: this method must not handle the event type or
   * mapper class, which are defined inside the {@link ObserverDefinition} or by the annotation {@link Observer}, cause
   * this decision is done already at the point, where this method is called. This method should return the decision
   * based on some more complex information by usong the context or the event properties, for instance
   * 
   * @param event
   * @param context
   * @return true, if event shall be handled, false otherwise
   */
  boolean canHandleEvent(IObserverEvent event, IObserverContext context);

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
