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

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.IAccessResult;
import de.braintags.vertx.jomnigate.dataaccess.IDataAccessObject;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDeleteResult;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.mapping.IMapper;
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
   * @param datastore
   * @return
   */
  public static IObserverEvent createEvent(ObserverEventType eventType, Object entity, IAccessResult accessResult,
      IDataAccessObject<?> accessObject, IDataStore<?, ?> datastore) {
    return new DefaultObserverEvent(eventType, entity, accessResult, accessObject, datastore);
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
   * <LI>{@link ObserverEventType#BEFORE_MAPPING} the class to be mapped
   * <LI>{@link ObserverEventType#AFTER_MAPPING} the {@link IMapper} which was created
   * <LI>{@link ObserverEventType#BEFORE_SAVE} the instance to be saved
   * <LI>{@link ObserverEventType#AFTER_SAVE} the instance, which was saved
   * <LI>{@link ObserverEventType#BEFORE_LOAD} null
   * <LI>{@link ObserverEventType#AFTER_LOAD} the instance, which was loaded
   * <LI>{@link ObserverEventType#BEFORE_DELETE} the instance, which shall be deleted
   * <LI>{@link ObserverEventType#AFTER_DELETE} the instance, which was deleted
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
   * <LI>{@link ObserverEventType#BEFORE_MAPPING} null
   * <LI>{@link ObserverEventType#AFTER_MAPPING} null
   * <LI>{@link ObserverEventType#BEFORE_SAVE} {@link IWriteResult}
   * <LI>{@link ObserverEventType#AFTER_SAVE} {@link IWriteResult}
   * <LI>{@link ObserverEventType#BEFORE_LOAD} null
   * <LI>{@link ObserverEventType#AFTER_LOAD} {@link IQueryResult}
   * <LI>{@link ObserverEventType#BEFORE_DELETE} {@link IDeleteResult}
   * <LI>{@link ObserverEventType#AFTER_DELETE} {@link IDeleteResult}
   * </UL>
   * 
   * @return
   */
  IAccessResult getAccessResult();

  /**
   * Get the data access object which is combined to the current event
   * 
   * <UL>
   * <LI>{@link ObserverEventType#BEFORE_MAPPING} null
   * <LI>{@link ObserverEventType#AFTER_MAPPING} null
   * <LI>{@link ObserverEventType#BEFORE_SAVE} {@link IWrite}
   * <LI>{@link ObserverEventType#AFTER_SAVE} {@link IWrite}
   * <LI>{@link ObserverEventType#BEFORE_LOAD} {@link IQuery}
   * <LI>{@link ObserverEventType#AFTER_LOAD} {@link IQuery}
   * <LI>{@link ObserverEventType#BEFORE_DELETE} {@link IDelete}
   * <LI>{@link ObserverEventType#AFTER_DELETE} {@link IDelete}
   * </UL>
   * 
   * @return
   */
  IDataAccessObject<?> getAccessObject();

  /**
   * The datastore, which can be used by an observer to execute further actions
   * 
   * @return
   */
  IDataStore<?, ?> getDataStore();

}
