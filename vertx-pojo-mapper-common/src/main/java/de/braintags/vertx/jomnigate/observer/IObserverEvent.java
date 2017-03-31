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
   * Create an instance of {@link IObserverEvent}. The content of the event depends on the {@link ObserverEventType},
   * which is processed. See the method descriptions for further information.
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
   * Get the event type which is executed.
   * 
   * @return
   */
  ObserverEventType getEventType();

  /**
   * Get the source object, which shall be handled. The content differs depending to the event type, which is processed:
   * 
   * <UL>
   * <LI>{@link ObserverEventType#BEFORE_MAPPING}
   * <LI>{@link ObserverEventType#AFTER_MAPPING}
   * <LI>{@link ObserverEventType#BEFORE_SAVE}
   * <LI>{@link ObserverEventType#AFTER_SAVE}
   * <LI>{@link ObserverEventType#BEFORE_LOAD}
   * <LI>{@link ObserverEventType#AFTER_LOAD}
   * <LI>{@link ObserverEventType#BEFORE_DELETE}
   * <LI>{@link ObserverEventType#AFTER_DELETE}
   * </UL>
   * 
   * @return
   */
  Object getSource();

  /**
   * Get the instance of {@link IAccessResult} for the current event. The content differs depending to the event type,
   * which is processed:
   * 
   * <UL>
   * <LI>{@link ObserverEventType#BEFORE_MAPPING}
   * <LI>{@link ObserverEventType#AFTER_MAPPING}
   * <LI>{@link ObserverEventType#BEFORE_SAVE}
   * <LI>{@link ObserverEventType#AFTER_SAVE}
   * <LI>{@link ObserverEventType#BEFORE_LOAD}
   * <LI>{@link ObserverEventType#AFTER_LOAD}
   * <LI>{@link ObserverEventType#BEFORE_DELETE}
   * <LI>{@link ObserverEventType#AFTER_DELETE}
   * </UL>
   * 
   * @return
   */
  IAccessResult getAccessResult();

  /**
   * Get the data access object which is combined to the current event
   * 
   * <UL>
   * <LI>{@link ObserverEventType#BEFORE_MAPPING}
   * <LI>{@link ObserverEventType#AFTER_MAPPING}
   * <LI>{@link ObserverEventType#BEFORE_SAVE}
   * <LI>{@link ObserverEventType#AFTER_SAVE}
   * <LI>{@link ObserverEventType#BEFORE_LOAD}
   * <LI>{@link ObserverEventType#AFTER_LOAD}
   * <LI>{@link ObserverEventType#BEFORE_DELETE}
   * <LI>{@link ObserverEventType#AFTER_DELETE}
   * </UL>
   * 
   * @return
   */
  IDataAccessObject<?> getAccessObject();
}
