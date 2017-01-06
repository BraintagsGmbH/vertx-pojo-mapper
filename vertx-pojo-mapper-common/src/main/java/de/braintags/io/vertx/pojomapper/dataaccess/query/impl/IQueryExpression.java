/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryPart;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
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

  void setNativeCommand(Object nativeCommand);

  void setMapper(IMapper<?> mapper);

  IMapper<?> getMapper();

  void buildQueryExpression(IQueryPart queryPart, Handler<AsyncResult<Void>> handler);

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
   *          the first record
   */
  void setLimit(int limit, int start);

}
