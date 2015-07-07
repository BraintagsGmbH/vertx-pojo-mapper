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

package de.braintags.io.vertx.pojomapper.dataaccess;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;

/**
 * IWrite is responsible for all write actions into the connected datasource. It performs inserts and updates.
 * 
 * @author Michael Remme
 * 
 */

public interface IWrite<T> extends IDataAccessObject<T> {

  /**
   * Add an entity to be saved
   * 
   * @param mapper
   *          the mapper to be saved
   */
  public void add(final T mapper);

  /**
   * Save the entities inside the current instance
   * 
   * @param resultHandler
   *          a handler, which will receive information about the save result
   */
  public void save(Handler<AsyncResult<IWriteResult>> resultHandler);

  /**
   * This object is created by a save action and contains the information about the action
   * 
   * 
   * @author Michael Remme
   *
   */
  interface IWriteResult {

    /**
     * Get the instance of {@link IStoreObject}, which was created during the save action
     * 
     * @return
     */
    public IStoreObject<?> getStoreObject();

    /**
     * Get the id of the saved object
     * 
     * @return
     */
    public Object getId();
  }
}
