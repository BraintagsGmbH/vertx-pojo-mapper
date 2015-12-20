/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.dataaccess.query;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.IDataAccessObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Define and execute queries inside the connected {@link IDataStore}
 * 
 * @author Michael Remme
 * @param <T>
 *          the underlaying mapper class
 */

public interface IQuery<T> extends IDataAccessObject<T>, IQueryContainer {

  /**
   * Execute the query
   * 
   * @param resultHandler
   *          contains the {@link IQueryResult}
   */
  public void execute(Handler<AsyncResult<IQueryResult<T>>> resultHandler);

  /**
   * Execute the query by counting the fitting objects
   * 
   * @param resultHandler
   *          contains the {@link IQueryCountResult}
   */
  public void executeCount(Handler<AsyncResult<IQueryCountResult>> resultHandler);

  /**
   * Execute the query with the option explain and sends back the suitable information
   * 
   * @param resultHandler
   *          contains the {@link IQueryResult}
   */
  public void executeExplain(Handler<AsyncResult<IQueryResult<T>>> resultHandler);

  /**
   * Set the maximum number of records to be returned
   * 
   * @param limit
   * @return the query itself for fluent access
   */
  public IQuery<T> setLimit(int limit);

  /**
   * Set the first record to be returned
   * 
   * @param start
   * @return the query itself for fluent access
   */
  public IQuery<T> setStart(int start);

  /**
   * If {@link #setLimit(int)} is defined with a value > 0 and this value is set to true, then the
   * {@link IQueryResult#getCompleteResult()} will return the fitting value
   * 
   * @param start
   * @return the query itself for fluent access
   */
  public IQuery<T> setReturnCompleteCount(boolean ret);

}
