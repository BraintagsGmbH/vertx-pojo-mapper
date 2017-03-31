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

import java.util.List;

import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDeleteResult;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.init.ObserverSettings;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.observer.impl.DefaultObserverHandler;
import io.vertx.core.Future;

/**
 * IObserverHandler is the member of an IMapper which keeps the information about registered {@link IObserver} and
 * executes the observers, which are registered for an event for the parent mapper
 * 
 * @author Michael Remme
 * 
 */
public interface IObserverHandler {

  /**
   * Create a new instance of {@link IObserverHandler}
   * 
   * @param mapper
   *          the mapper to be used
   * @return
   */
  public static IObserverHandler createInstance(IMapper<?> mapper) {
    return new DefaultObserverHandler(mapper);
  }

  /**
   * Get all observers, which are registered for the current mapper and the given event. The list should be sorted by
   * priority of the underlaying {@link ObserverSettings}
   * 
   * @param event
   * @return a list of all fitting IObserver
   */
  List<IObserver> getObserver(ObserverEventType event);

  /**
   * Performs the event {@link ObserverEventType#BEFORE_SAVE} to the records in the {@link IWrite}
   * 
   * @param writeObject
   * @param context
   * @return
   */
  <T> Future<Void> handleBeforeSave(IWrite<T> writeObject, IObserverContext context);

  /**
   * Performs the event {@link ObserverEventType#AFTER_SAVE} to the records in the {@link IWrite}
   * 
   * @param writeObject
   * @param writeResult
   * @param context
   * @return
   */
  <T> Future<Void> handleAfterSave(IWrite<T> writeObject, IWriteResult writeResult, IObserverContext context);

  /**
   * Performs the event {@link ObserverEventType#BEFORE_LOAD} to the records in the {@link IWrite}
   * 
   * @param queryObject
   * @param context
   * @return
   */
  <T> Future<Void> handleBeforeLoad(IQuery<T> queryObject, IObserverContext context);

  /**
   * Performs the event {@link ObserverEventType#AFTER_LOAD} to the records in the {@link IQuery}
   * 
   * @param queryObject
   * @param queryResult
   * @param context
   * @return
   */
  <T> Future<Void> handleAfterLoad(IQuery<T> queryObject, IQueryResult<T> queryResult, IObserverContext context);

  /**
   * Performs the event {@link ObserverEventType#BEFORE_DELETE} to the records in the {@link IDelete}
   * 
   * @param deleteObject
   * @param context
   * @return
   */
  <T> Future<Void> handleBeforeDelete(IDelete<T> deleteObject, IObserverContext context);

  /**
   * Performs the event {@link ObserverEventType#AFTER_DELETE} to the records in the {@link IDelete}
   * 
   * @param deleteObject
   * @param deleteResult
   * @param context
   * @return
   */
  <T> Future<Void> handleAfterDelete(IDelete<T> deleteObject, IDeleteResult deleteResult, IObserverContext context);

}
