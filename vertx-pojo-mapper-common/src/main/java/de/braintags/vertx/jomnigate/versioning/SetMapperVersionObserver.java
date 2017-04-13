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
package de.braintags.vertx.jomnigate.versioning;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.init.ObserverMapperSettings;
import de.braintags.vertx.jomnigate.init.ObserverSettings;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import io.vertx.core.Future;

/**
 * The observer is responsible to set the version value of all entities, where the field {@link Entity#version()} is
 * defined > 0. The observer is programmatically added to the observer system with the highest priority.
 * 
 * @author Michael Remme
 * 
 */
public class SetMapperVersionObserver implements IObserver {

  public static ObserverSettings<SetMapperVersionObserver> createObserverSettings() {
    ObserverSettings<SetMapperVersionObserver> settings = new ObserverSettings<>(SetMapperVersionObserver.class);
    settings.setPriority(Integer.MAX_VALUE);
    settings.getEventTypeList().add(ObserverEventType.BEFORE_SAVE);
    ObserverMapperSettings ms = new ObserverMapperSettings(IMapperVersion.class.getName());
    ms.setInstanceOf(true);
    settings.getMapperSettings().add(ms);
    return settings;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.observer.IObserver#canHandleEvent(de.braintags.vertx.jomnigate.observer.
   * IObserverEvent, de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  public boolean canHandleEvent(IObserverEvent event, IObserverContext context) {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.observer.IObserver#handleEvent(de.braintags.vertx.jomnigate.observer.IObserverEvent,
   * de.braintags.vertx.jomnigate.observer.IObserverContext)
   */
  @Override
  public Future<Void> handleEvent(IObserverEvent event, IObserverContext context) {
    return Future.failedFuture(new UnsupportedOperationException());
  }

}
