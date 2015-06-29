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

package de.braintags.io.vertx.pojomapper;

import de.braintags.io.vertx.pojomapper.dataaccess.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.IWrite;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.ITypeHandler;

/**
 * IDataStore contains information about the destination datastore and creates the handler objects
 * 
 * @author Michael Remme
 * 
 */

public interface IDataStore {

  /**
   * Returns a new {@link IQuery} bound to the given mapper
   * 
   * @param mapper
   *          the mapper class, where the new instance shall be bound to
   * @return a new instance of {@link IWrite}
   */
  <T> IQuery<T> createQuery(Class<T> mapper);

  /**
   * Create a new {@link IWrite} bound to the given mapper
   * 
   * @param mapper
   *          the mapper class, where the new instance shall be bound to
   * @return a new instance of {@link IWrite}
   */
  <T> IWrite<T> createWrite(Class<T> mapper);

  /**
   * Create a new {@link IDelete} bound to the given mapper
   * 
   * @param mapper
   *          the mapper class, where the new instance shall be bound to
   * @return a new instance of {@link IWrite}
   */
  <T> IDelete<T> createDelete(Class<T> mapper);

  /**
   * Get or create the {@link IMapperFactory} used by this implementation
   * 
   * @return
   */
  IMapperFactory getMapperFactory();

  /**
   * Get the propriate {@link ITypeHandler} for the field
   * 
   * @param field
   * @return
   */
  ITypeHandler getTypeHandler(IField field);
}
