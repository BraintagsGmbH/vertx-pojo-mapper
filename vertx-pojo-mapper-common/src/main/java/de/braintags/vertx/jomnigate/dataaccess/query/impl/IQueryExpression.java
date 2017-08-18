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
package de.braintags.vertx.jomnigate.dataaccess.query.impl;

import java.util.List;

import de.braintags.vertx.jomnigate.dataaccess.query.IFieldValueResolver;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.ISortDefinition;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * IQueryExpression is the datastore specific result of an executed {@link AbstractQueryRambler}, which later on is used
 * to execute the query on the datastore
 *
 * @author Michael Remme
 *
 */
public interface IQueryExpression {

  /**
   * Set a direct native commend. The value must be of a type that the datastore can handle natively.
   *
   * @param nativeCommand
   */
  void setNativeCommand(Object nativeCommand);

  /**
   * Set the mapper for this query expression
   *
   * @param mapper
   */
  void setMapper(IMapper<?> mapper);

  /**
   * Get the mapper for this query expression
   *
   * @return
   */
  IMapper<?> getMapper();

  /**
   * Build the concrete query expression from a search condition of a query
   *
   * @param searchCondition
   *          the implementation independent search condition
   * @param resolver
   *          replaces potential variables in the search condition with an actual value, can be null
   * @param handler
   */
  void buildSearchCondition(ISearchCondition searchCondition, IFieldValueResolver resolver,
      Handler<AsyncResult<Void>> handler);

  /**
   * Adds the given {@link ISortDefinition} into the current instance like it is needed by the implementation
   *
   * @param sortDef
   *          the sort definition to be added
   * @return the IQueryExpression itself for fluent usage
   */
  IQueryExpression addSort(ISortDefinition<?> sortDef);

  /**
   * Set the limit and the offset ( start ) of a selection
   *
   * @param limit
   *          the limit of the selection
   * @param offset
   *          the offset (starting position) for this query
   */
  void setLimit(int limit, int offset);

  /**
   * @return the limit for this query
   */
  public int getLimit();

  /**
   * @return the offset (starting position) for this query
   */
  public int getOffset();

  /**
   * Set the use fields that restrict the result of the query
   * 
   * @param useFields
   *          the use fields to set
   */
  void setUseFields(List<String> useFields);
}
