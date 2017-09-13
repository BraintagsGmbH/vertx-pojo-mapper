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
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.observer.IObserver;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.jomnigate.versioning.SetMapperVersionObserver;

/**
 * Contains all {@link ObserverDefinition}s of the running system
 * 
 * @author Michael Remme
 * 
 */
@JsonIgnoreProperties(value = { "empty" })
public class ObserverSettings {
  @JsonProperty(value = "observerDefinitions")
  private final List<ObserverDefinition<?>> observerDefinitions = new ArrayList<>();

  /**
   * Default creator which initializes the default observer
   */
  public ObserverSettings() {
    initDefaultObserver();
  }

  /**
   * Creates a cloned intance
   * 
   * @param source
   */
  public ObserverSettings(final ObserverSettings source) {
    for (ObserverDefinition<?> observer : source.getObserverDefinitions()) {
      observerDefinitions.add(observer.deepCopy());
    }
  }

  /**
   * Initialize needed instances of {@link IObserver} which must exist inside the current implementation
   */
  protected void initDefaultObserver() {
    observerDefinitions.add(SetMapperVersionObserver.createObserverSettings());
  }

  /**
   * Get the ObserverDefinition which contains an IObserver with the given class
   * 
   * @param observerClass
   * @return
   */
  public ObserverDefinition<?> getDefinition(final Class<? extends IObserver> observerClass) {
    for (ObserverDefinition os : observerDefinitions) {
      if (os.getObserverClass() == observerClass) {
        return os;
      }
    }
    return null;
  }

  /**
   * Get all {@link ObserverDefinition} which are fitting the given mapper class for the given event
   * 
   * @param mapperClass
   * @param eventType
   * @return
   */
  public List<ObserverDefinition<?>> getObserverDefinitions(final Class<?> mapperClass,
      final ObserverEventType eventType) {
    List<ObserverDefinition<?>> tmpList = new ArrayList<>();
    List<ObserverDefinition<?>> osl = getObserverDefinitions();
    osl.stream().filter(os -> os.isApplicableFor(mapperClass) && os.isApplicableFor(eventType)).forEach(tmpList::add);
    return tmpList;
  }

  /**
   * Get all {@link ObserverDefinition} which are fitting the given mapper
   * 
   * @param mapper
   * @return
   */
  public List<ObserverDefinition<?>> getObserverDefinitions(final IMapper<?> mapper) {
    List<ObserverDefinition<?>> osl = getObserverDefinitions();
    return osl.stream().filter(os -> os.isApplicableFor(mapper)).collect(Collectors.toList());
  }

  /**
   * Get all defined {@link ObserverDefinition}
   * 
   * @return the observerSettings
   */
  private List<ObserverDefinition<?>> getObserverDefinitions() {
    return observerDefinitions;
  }

  /**
   * Add a new definition to the internal list
   * 
   * @param definition
   */
  public void add(final ObserverDefinition<?> definition) {
    observerDefinitions.add(definition);
  }

  /**
   * Reset the settings to their initial values
   */
  public void reset() {
    observerDefinitions.clear();
    initDefaultObserver();
  }

  public boolean isEmpty() {
    return observerDefinitions.isEmpty();
  }
}
