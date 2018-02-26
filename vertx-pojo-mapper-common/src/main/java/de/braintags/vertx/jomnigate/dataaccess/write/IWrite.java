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
package de.braintags.vertx.jomnigate.dataaccess.write;

import java.util.Collection;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.IDataAccessObject;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * IWrite is responsible for all write actions into the connected datasource. It performs inserts and updates.
 *
 * @author Michael Remme
 * @param <T>
 *          the unt´derlaing mapper class
 */

public interface IWrite<T> extends IDataAccessObject<T> {

  /**
   * Add an entity to be saved
   *
   * @param mapper
   *          the mapper to be saved
   */
  public void add(T mapper);

  /**
   * Add a list of entities to be saved
   *
   * @param mapperList
   *          the objects to be saved
   */
  public void addAll(Collection<T> mapperList);

  /**
   * Save the entities inside the current instance
   *
   * @param resultHandler
   *          a handler, which will receive information about the save result
   */
  public void save(Handler<AsyncResult<IWriteResult>> resultHandler);

  /**
   * Get the number of records to be saved
   *
   * @return the count
   */
  public int size();

  /**
   * Defines the {@link IQuery} which is used match documents to update.
   *
   * @param query
   *          the {@link IQuery} which shall be used to specify the records to be updated from the {@link IDataStore}
   */
  public void setQuery(IQuery<T> query);

  public void setPartialUpdate(boolean partialUpdate);

}
