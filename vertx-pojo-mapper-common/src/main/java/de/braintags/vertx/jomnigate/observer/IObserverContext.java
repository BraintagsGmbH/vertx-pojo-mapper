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

import de.braintags.vertx.jomnigate.observer.impl.DefaultObserverContext;

/**
 * The IObserverContext is created before the observers of a mapper are executed and delivered to the execution of each
 * event, which is processed
 * 
 * @author Michael Remme
 * 
 */
public interface IObserverContext {

  /**
   * Create a new instance
   * 
   * @return
   */
  public static IObserverContext createInstance() {
    return new DefaultObserverContext();
  }

  /**
   * Get a value specified by the given key
   * 
   * @param key
   * @return an instance or null
   */
  Object get(String key);

  /**
   * Get a value specified by the given key. If key does not exist, return the default value
   * 
   * @param key
   * @param defaultValue
   * @return an instance or defaultValue
   */
  <V> V get(String key, V defaultValue);

  /**
   * Store an instance into the context referenced by the given key
   * 
   * @param key
   * @param value
   * @return the previous value or null
   */
  Object put(String key, Object value);

}
