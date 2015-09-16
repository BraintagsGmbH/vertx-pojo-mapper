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
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class LogicContainer<T extends IQueryContainer> extends AbstractQueryContainer<IQueryContainer>
    implements ILogicContainer<T>, IRamblerSource {
  private List<Object> filters = new ArrayList<Object>();
  private QueryLogic logic;

  /**
   * 
   */
  public LogicContainer(T parent, QueryLogic logic) {
    super(parent);
    this.logic = logic;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#field(java.lang.String)
   */
  @Override
  public IFieldParameter<LogicContainer<T>> field(String fieldName) {
    FieldParameter<LogicContainer<T>> param = new FieldParameter<LogicContainer<T>>(this,
        getQuery().getMapper().getField(fieldName));
    filters.add(param);
    return param;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#and(java.lang.String)
   */
  @Override
  public IFieldParameter<LogicContainer<IQueryContainer>> and(String fieldName) {
    LogicContainer<IQueryContainer> container = new LogicContainer<IQueryContainer>(this, QueryLogic.AND);
    filters.add(container);
    return container.field(fieldName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#or(java.lang.String)
   */
  @Override
  public IFieldParameter<LogicContainer<IQueryContainer>> or(String fieldName) {
    LogicContainer<IQueryContainer> container = new LogicContainer<IQueryContainer>(this, QueryLogic.OR);
    filters.add(container);
    return container.field(fieldName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer#getLogic()
   */
  @Override
  public QueryLogic getLogic() {
    return logic;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T parent() {
    return (T) super.parent();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IRamblerSource#applyTo(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.impl.IQueryRambler)
   */
  @Override
  public void applyTo(IQueryRambler rambler, Handler<AsyncResult<Void>> resultHandler) {
    rambler.start(this);
    if (filters.isEmpty()) {
      finishCounter(rambler, resultHandler);
      return;
    }
    ErrorObject<Void> error = new ErrorObject<Void>();
    CounterObject co = new CounterObject(filters.size());
    for (Object filter : filters) {
      handleFilter(rambler, resultHandler, filter, error, co);
      if (error.isError()) {
        return;
      }
    }
  }

  private void handleFilter(IQueryRambler rambler, Handler<AsyncResult<Void>> resultHandler, Object filter,
      ErrorObject<Void> error, CounterObject co) {
    if (!(filter instanceof IRamblerSource)) {
      error.setThrowable(
          new UnsupportedOperationException("NOT AN INSTANCE OF IRamblerSource: " + filter.getClass().getName()));
      error.handleError(resultHandler);
      return;
    }

    ((IRamblerSource) filter).applyTo(rambler, result -> {
      if (result.failed()) {
        error.setThrowable(result.cause());
        error.handleError(resultHandler);
        return;
      }
      if (co.reduce()) { // last element in the list
        finishCounter(rambler, resultHandler);
      }
    });
  }

  private void finishCounter(IQueryRambler rambler, Handler<AsyncResult<Void>> resultHandler) {
    rambler.stop(this);
    resultHandler.handle(Future.succeededFuture());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#getChildren()
   */
  @Override
  public List<Object> getChildren() {
    return filters;
  }

}
