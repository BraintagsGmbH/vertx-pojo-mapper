/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.json.jackson.deserializer.referenced;

import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

import io.vertx.core.Future;

/**
 * Bridge between async process of datastore and synchronous execution of jackson. Referenced objects are loaded in an
 * async way, the future for the loading is stored inside a ReferencedPostHandler. The JsonStoreObject checks for
 * existing ReferencedPostHandlers, waits for the integrated futures within a Composite and then executes some actions
 * to store the resulting records into the suitable instance.
 * 
 * @author Michael Remme
 *
 */
public class ReferencedPostHandler {
  private Future future;
  private Object instance;
  private SettableBeanProperty beanProperty;

  /**
   * An instance which is just used to await the finish of the given Future. All records are stored alredy into the
   * correct result. This can be used for Collections or Maps, where synchronously the correct, empty instance is
   * returned and afterwards records are loaded and stored into this instance
   * 
   * @param future
   * @param instance
   * @param beanProperty
   */
  public ReferencedPostHandler(Future future) {
    this.future = future;
  }

  /**
   * An instance which is used to add the loaded record into the given instance by using the
   * {@link SettableBeanProperty}
   * 
   * @param future
   * @param instance
   * @param beanProperty
   */
  public ReferencedPostHandler(Future future, Object instance, SettableBeanProperty beanProperty) {
    this.future = future;
    this.instance = instance;
    this.beanProperty = beanProperty;
  }

  /**
   * @return the future
   */
  public Future getFuture() {
    return future;
  }

  /**
   * Get the instance, which is used to store the values of the Future.
   * If it is null, then the post process will only wait the finishing of the future
   * 
   * @return the instance
   */
  public Object getInstance() {
    return instance;
  }

  /**
   * @return the beanProperty
   */
  public SettableBeanProperty getBeanProperty() {
    return beanProperty;
  }

}