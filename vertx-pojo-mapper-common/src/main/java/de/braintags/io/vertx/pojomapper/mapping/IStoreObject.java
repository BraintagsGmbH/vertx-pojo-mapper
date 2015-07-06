/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mapping;

import io.vertx.codegen.annotations.Fluent;
import de.braintags.io.vertx.pojomapper.IDataStore;

/**
 * The IStoreObject is building the bridge between the propriate format coming from out of the used {@link IDataStore}
 * and the rest of the application. This can be a Json object or an internal format for later use of a ResultSet for
 * instance
 * 
 * @author Michael Remme
 * 
 */

public interface IStoreObject<T> {

  /**
   * Get the defined property in the propriate format of the current driver
   * 
   * @param property
   *          the {@link IField} describing the property
   * @return the value of the property in the propriate format of the current implementation of the {@link IDataStore}
   */
  public Object get(IField field);

  /**
   * Adds a new property into the internal container
   * 
   * @param property
   *          the {@link IField} used to describe the property
   * @param value
   *          the value to be stored in the proprietary format
   * @return a reference to itself
   */
  @Fluent
  public IStoreObject<T> put(IField field, Object value);

  /**
   * Get the raw container, which stores the information
   * 
   * @return the container
   */
  public T getContainer();
}
