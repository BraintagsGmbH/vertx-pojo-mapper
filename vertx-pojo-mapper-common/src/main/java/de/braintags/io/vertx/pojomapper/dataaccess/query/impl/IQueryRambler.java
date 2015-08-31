/*
 * Copyright 2015 Braintags GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * You may elect to redistribute this code under this licenses.
 */

package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;

/**
 * The IQueryRambler is used as argument by {@link Query#executeQueryRambler(IQueryRambler)}. This method traverses
 * through all parts of the query definition and calls the specified methods of this interface. An implementation will
 * use this interface to generate the native, database specific query object
 * 
 * @author Michael Remme
 * 
 */

public interface IQueryRambler {

  /**
   * Start applying the {@link IQuery} itself
   * 
   * @param query
   *          the query to be applied
   */
  void start(IQuery<?> query);

  /**
   * Stop applying the {@link IQuery} itself
   * 
   * @param query
   *          the query to be applied
   */
  void stop(IQuery<?> query);

  /**
   * Start applying an {@link ILogicContainer}
   * 
   * @param container
   *          the container to be applied
   */
  void start(ILogicContainer<?> container);

  /**
   * Stop applying an {@link ILogicContainer}
   * 
   * @param container
   *          the container to be applied
   */
  void stop(ILogicContainer<?> container);

  /**
   * Start applying an {@link IFieldParameter}
   * 
   * @param fieldParameter
   *          the field paremeter to be applied
   * @param resultHandler
   *          the handler to be called
   */
  void start(IFieldParameter<?> fieldParameter, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Stop applying an {@link IFieldParameter}
   * 
   * @param fieldParameter
   *          the field paremeter to be applied
   */
  void stop(IFieldParameter<?> fieldParameter);

}
