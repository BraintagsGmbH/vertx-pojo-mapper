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

import de.braintags.vertx.jomnigate.dataaccess.IAccessResult;
import de.braintags.vertx.jomnigate.dataaccess.IDataAccessObject;
import de.braintags.vertx.jomnigate.observer.impl.DefaultObserverEvent;

/**
 * The {@link IObserverEvent} contains all information needed to process an event by an {@link IObserver}
 * 
 * @author Michael Remme
 * 
 */
public interface IObserverEvent {

  /**
   * Create an instance of {@link IObserverEvent}
   * 
   * @param eventType
   * @param entity
   * @param accessResult
   * @param accessObject
   * @return
   */
  public static IObserverEvent createEvent(ObserverEventType eventType, Object entity, IAccessResult accessResult,
      IDataAccessObject<?> accessObject) {
    return new DefaultObserverEvent(eventType, entity, accessResult, accessObject);
  }

  /**
   * Get the event type which is executed
   * 
   * @return
   */
  ObserverEventType getEventType();

  /**
   * Get the entity, which was executed. In case of {@link ObserverEventType#BEFORE_LOAD} this can be null
   * 
   * @return
   */
  Object getEntity();

  /**
   * Get the instance of {@link IAccessResult} for the current event. This depends on the executed event.
   * 
   * @return
   */
  IAccessResult getAccessResult();

  /**
   * Get the data access object which is combined to the current event
   * 
   * @return
   */
  IDataAccessObject<?> getAccessObject();
}
