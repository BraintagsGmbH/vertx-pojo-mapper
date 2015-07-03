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

import de.braintags.io.vertx.pojomapper.IDataStore;

/**
 * The IStoreObject is building the bridge between the propriate format coming from out of the used {@link IDataStore}
 * and the rest of the application. This can be a Json object or an internal format for later use of a ResultSet for
 * instance
 * 
 * @author Michael Remme
 * 
 */

public interface IStoreObject {

  /**
   * 
   * @param property
   * @return
   */
  public Object get(IField field);

  /**
   * Adds a new property into the internal container
   * 
   * @param property
   * @param value
   * @return
   */
  public IStoreObject put(IField field, Object value);
}
