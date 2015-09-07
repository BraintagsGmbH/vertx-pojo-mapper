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

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.IDataAccessObject;

/**
 * Define and execute queries inside the connected {@link IDataStore}
 * 
 * @author Michael Remme
 * 
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

}
