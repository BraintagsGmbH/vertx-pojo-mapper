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
import java.util.Properties;

import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;

/**
 * Used to define application of an {@link IObserver} instance
 * 
 * @author Michael Remme
 * @param <T>
 *          the type of IOberver used by this implementation
 */
public class ObserverDefinition<T extends IObserver> {
  private Class<T> observerClass;
  private List<ObserverEventType> eventTypeList = new ArrayList<>();
  private List<ObserverMapperSettings> mapperSettings = new ArrayList<>();
  private int priority;
  private Properties observerProperties = new Properties();

  @SuppressWarnings("unused")
  private ObserverDefinition() {
    // not externally usable
  }

  public ObserverDefinition(final Class<T> observerClass) {
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
  public void setObserverClass(final Class<T> observerClass) {
    this.observerClass = observerClass;
  }

  /**
   * The list of all events, where the defined observer shall be executed on
   * 
   * @return the eventList
   */
  public List<ObserverEventType> getEventTypeList() {
    return eventTypeList;
  }

  /**
   * The list of all events, where the defined observer shall be executed on
   * 
   * @param eventList
   *          the eventList to set
   */
  @SuppressWarnings("unused")
  private void setEventTypeList(final List<ObserverEventType> eventTypeList) {
    this.eventTypeList = eventTypeList;
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
  private void setMapperSettings(final List<ObserverMapperSettings> mapperSettings) {
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
  public void setPriority(final int priority) {
    this.priority = priority;
  }

  /**
   * properties can be used to define the behaviour of an implementation of {@link IObserver}
   * 
   * @return the observerProperties
   */
  public final Properties getObserverProperties() {
    return observerProperties;
  }

  /**
   * properties can be used to define the behaviour of an implementation of {@link IObserver}
   * 
   * @param observerProperties
   *          the observerProperties to set
   */
  public final void setObserverProperties(Properties observerProperties) {
    this.observerProperties = observerProperties;
  }

  /**
   * Checks wether the current settings are applicable to the given IMapper. The definition is applicable, if no mapper
   * settings are defined or if mapper settings are defined and one is fitting
   * 
   * @param mapperClass
   * @return
   */
  public boolean isApplicableFor(final Class<?> mapperClass) {
    if (getMapperSettings().isEmpty()) {
      return true;
    }
    return getMapperSettings().stream().filter(s -> s.isApplicableFor(mapperClass)).findFirst().isPresent();
  }

  /**
   * Checks wether the current settings are applicable to the given IMapper. The definition is applicable, if no mapper
   * settings are defined or if mapper settings are defined and one is fitting
   * 
   * @param mapper
   * @return
   */
  public boolean isApplicableFor(final IMapper<?> mapper) {
    if (getMapperSettings().isEmpty()) {
      return true;
    }
    return getMapperSettings().stream().filter(s -> s.isApplicableFor(mapper)).findFirst().isPresent();
  }

  /**
   * Checks wether the current settings are applicable for the given ObserverEventType. The definition is applicable, if
   * no event type is defined or if at least one event type is defined, which is fitting the given one
   * 
   * @param mapper
   * @return
   */
  public boolean isApplicableFor(final ObserverEventType eventType) {
    if (getEventTypeList().isEmpty()) {
      return true;
    }
    return getEventTypeList().stream().filter(evt -> evt.equals(eventType)).findFirst().isPresent();
  }

  /**
   * Creates a deep (recursive) copy of the ObserverSettings
   */
  public ObserverDefinition<T> deepCopy() {
    ObserverDefinition<T> res = new ObserverDefinition<>(getObserverClass());
    res.eventTypeList.addAll(getEventTypeList());
    res.observerProperties.putAll(observerProperties);
    res.priority = getPriority();

    for (ObserverMapperSettings mapperSetting : mapperSettings) {
      res.mapperSettings.add(mapperSetting.deepCopy());
    }

    return res;
  }

}
