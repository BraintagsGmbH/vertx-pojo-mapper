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

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.IAccessResult;
import de.braintags.vertx.jomnigate.dataaccess.IDataAccessObject;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;

/**
 * Default implementation for an {@link IObserverEvent}
 * 
 * @author Michael Remme
 * 
 */
public class DefaultObserverEvent implements IObserverEvent {
  private final ObserverEventType eventType;
  private final Object entity;
  private final IAccessResult accessResult;
  private final IDataAccessObject<?> accessObject;
  private IDataStore<?, ?> datastore;

  /**
   * 
   * @param eventType
   * @param entity
   * @param accessResult
   * @param accessObject
   * @param datastore
   */
  public DefaultObserverEvent(ObserverEventType eventType, Object entity, IAccessResult accessResult,
      IDataAccessObject<?> accessObject, IDataStore<?, ?> datastore) {
    this.eventType = eventType;
    this.entity = entity;
    this.accessResult = accessResult;
    this.accessObject = accessObject;
    this.datastore = datastore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.observer.IObserverEvent#getEventType()
   */
  @Override
  public ObserverEventType getEventType() {
    return eventType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.observer.IObserverEvent#getEntity()
   */
  @Override
  public Object getSource() {
    return entity;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.observer.IObserverEvent#getAccessResult()
   */
  @Override
  public IAccessResult getAccessResult() {
    return accessResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.observer.IObserverEvent#getAccessObject()
   */
  @Override
  public IDataAccessObject<?> getAccessObject() {
    return accessObject;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.observer.IObserverEvent#getDataStore()
   */
  @Override
  public IDataStore<?, ?> getDataStore() {
    return datastore;
  }

}
