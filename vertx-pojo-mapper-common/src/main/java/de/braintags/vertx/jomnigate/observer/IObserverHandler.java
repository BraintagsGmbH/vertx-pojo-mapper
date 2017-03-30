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

import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.init.ObserverSettings;
import io.vertx.core.Future;

/**
 * IObserverHandler is the member of an IMapper which keeps the information about registered {@link IObserver} and
 * executes the observer handling
 * 
 * @author Michael Remme
 * 
 */
public interface IObserverHandler {

  /**
   * Performs the event {@link ObserverEventType#BEFORE_SAVE} to the records in the {@link IWrite}
   * 
   * @param writeObject
   * @return
   */
  Future<Void> handleBeforeSave(IWrite<?> writeObject);

  /**
   * Performs the event {@link ObserverEventType#AFTER_SAVE} to the records in the {@link IWrite}
   * 
   * @param writeObject
   * @return
   */
  Future<Void> handleAfterSave(IWrite<?> writeObject, IWriteResult writeResult);

  /**
   * Get all observers, which are registered for the current mapper and the given event. The list should be sorted by
   * priority of the underlaying {@link ObserverSettings}
   * 
   * @return a list of all fitting IObserver
   */
  List<IObserver> getObserver(ObserverEventType event);

}
