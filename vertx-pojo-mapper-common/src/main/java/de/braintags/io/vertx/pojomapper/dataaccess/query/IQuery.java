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

public interface IQuery<T> extends IDataAccessObject<T> {

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
   *          contains the {@link IQueryResult}
   */
  public void executeCount(Handler<AsyncResult<IQueryResult<T>>> resultHandler);

  /**
   * Add a query for a specified field
   * 
   * @param fieldName
   *          the name of the field
   * @return an instance of {@link IFieldParameter}
   */
  public IFieldParameter field(String fieldName);

}
