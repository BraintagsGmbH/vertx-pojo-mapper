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

import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.Query;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

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

  /**
   * Start applying an {@link ISortDefinition}
   * 
   * @param sortDefinition
   *          the sortdefinition to be applied
   */
  void start(ISortDefinition<?> sortDefinition);

  /**
   * Stop applying an {@link ISortDefinition}
   * 
   * @param sortDefinition
   *          the sortdefinition to be applied
   */
  void stop(ISortDefinition<?> sortDefinition);

}
