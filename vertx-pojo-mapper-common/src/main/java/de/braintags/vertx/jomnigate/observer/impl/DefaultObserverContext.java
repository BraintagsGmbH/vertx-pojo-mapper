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

import java.util.HashMap;
import java.util.Map;

import de.braintags.vertx.jomnigate.observer.IObserverContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class DefaultObserverContext implements IObserverContext {
  private Map<String, Object> valueMap = new HashMap<>();

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.observer.IObserverContext#get(java.lang.String)
   */
  @Override
  public Object get(String key) {
    return valueMap.get(key);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.observer.IObserverContext#put(java.lang.String, java.lang.Object)
   */
  @Override
  public Object put(String key, Object value) {
    return valueMap.put(key, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.observer.IObserverContext#get(java.lang.String, java.lang.Object)
   */
  @Override
  public <V> V get(String key, V defaultValue) {
    return (V) valueMap.getOrDefault(key, defaultValue);
  }

}
