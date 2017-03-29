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
package de.braintags.vertx.jomnigate.init;

import java.util.ArrayList;
import java.util.List;

import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.IObserverEvent;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;

/**
 * Used to define application of an {@link IObserver} instance
 * 
 * @author Michael Remme
 * 
 */
public class ObserverSettings<T extends IObserver> {
  private Class<T> observerClass;
  private List<IObserverEvent> eventList = new ArrayList<>();
  private List<ObserverMapperSettings> mapperSettings = new ArrayList<>();
  private int priority;

  @SuppressWarnings("unused")
  private ObserverSettings() {
    // not externally usable
  }

  public ObserverSettings(Class<T> observerClass) {
    this.observerClass = observerClass;
  }

  /**
   * The class of the observer to be used
   * 
   * @return the observerClass
   */
  public Class<T> getObserverClass() {
    return observerClass;
  }

  /**
   * The class of the observer to be used
   * 
   * @param observerClass
   *          the observerClass to set
   */
  public void setObserverClass(Class<T> observerClass) {
    this.observerClass = observerClass;
  }

  /**
   * The list of all events, where the defined observer shall be executed on
   * 
   * @return the eventList
   */
  public List<IObserverEvent> getEventList() {
    return eventList;
  }

  /**
   * The list of all events, where the defined observer shall be executed on
   * 
   * @param eventList
   *          the eventList to set
   */
  @SuppressWarnings("unused")
  private void setEventList(List<IObserverEvent> eventList) {
    this.eventList = eventList;
  }

  /**
   * This list defines, on which mappers the current definition shall be executed
   * 
   * @return the mapperList
   */
  public List<ObserverMapperSettings> getMapperSettings() {
    return mapperSettings;
  }

  /**
   * This list defines, on which mappers the current definition shall be executed
   * 
   * @param mapperSettings
   *          the mapperList to set
   */
  @SuppressWarnings("unused")
  private void setMapperSettings(List<ObserverMapperSettings> mapperSettings) {
    this.mapperSettings = mapperSettings;
  }

  /**
   * The priority for the current definition will define the execution order
   * 
   * @return the priority
   */
  public int getPriority() {
    return priority;
  }

  /**
   * @param priority
   *          the priority to set
   */
  public void setPriority(int priority) {
    this.priority = priority;
  }

  /**
   * Checks wether the current settings are applyable to the given IMapper. The definition is applyable, if no mapper
   * settings are defined or if mapper settings are defined and one is fitting
   * 
   * @param mapper
   * @return
   */
  public boolean isApplyableFor(IMapper<?> mapper) {
    if (getMapperSettings().isEmpty()) {
      return true;
    }
    return getMapperSettings().stream().filter(s -> s.isApplyableFor(mapper)).findFirst().isPresent();
  }

  /**
   * Checks wether the current settings are applyable for the given ObserverEventType. The definition is applyable, if
   * no event type is defined or if at least one event type is defined, which is fitting the given one
   * 
   * @param mapper
   * @return
   */
  public boolean isApplyableFor(ObserverEventType eventType) {
    if (getEventList().isEmpty()) {
      return true;
    }
    return getEventList().stream().filter(event -> event.getEventType().equals(eventType)).findFirst().isPresent();
  }

}
