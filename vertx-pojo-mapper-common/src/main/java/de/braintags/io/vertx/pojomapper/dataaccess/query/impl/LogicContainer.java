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
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IRamblerSource;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;
import de.braintags.io.vertx.util.async.DefaultAsyncResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * The LogicContainer defines the {@link QueryLogic} of a query
 * 
 * @author Michael Remme
 * @param <T>
 *          the parent container of the current instance
 */

public class LogicContainer<T extends IQueryContainer> extends AbstractQueryContainer<IQueryContainer>
    implements ILogicContainer<T>, IRamblerSource {
  private List<Object> filters = new ArrayList<>();
  private QueryLogic logic;

  /**
   * Creates a new instance with the given parent container and the defined logic
   * 
   * @param parent
   * @param logic
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
    FieldParameter<LogicContainer<T>> param = new FieldParameter<>(this, getQuery().getMapper().getField(fieldName));
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
    LogicContainer<IQueryContainer> container = new LogicContainer<>(this, QueryLogic.AND);
    filters.add(container);
    return container.field(fieldName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#andOpen(java.lang.String)
   */
  @Override
  public IFieldParameter<? extends ILogicContainer<? extends IQueryContainer>> andOpen(String fieldName) {
    LogicContainer<IQueryContainer> container = new LogicContainer<>(this, QueryLogic.AND_OPEN);
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
    LogicContainer<IQueryContainer> container = new LogicContainer<>(this, QueryLogic.OR);
    filters.add(container);
    return container.field(fieldName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#orOpen(java.lang.String)
   */
  @Override
  public IFieldParameter<? extends ILogicContainer<? extends IQueryContainer>> orOpen(String fieldName) {
    LogicContainer<IQueryContainer> container = new LogicContainer<>(this, QueryLogic.OR_OPEN);
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
      rambler.stop(this);
      resultHandler.handle(Future.succeededFuture());
    } else {
      CompositeFuture cf = CompositeFuture.all(createFutureList(rambler));
      cf.setHandler(res -> {
        if (res.failed()) {
          resultHandler.handle(DefaultAsyncResult.fail(res.cause()));
        } else {
          rambler.stop(this);
          resultHandler.handle(DefaultAsyncResult.succeed());
        }
      });
    }
  }

  @SuppressWarnings("rawtypes")
  private List<Future> createFutureList(IQueryRambler rambler) {
    List<Future> fl = new ArrayList<>();
    for (Object filter : filters) {
      fl.add(handleFilter(rambler, filter));
    }
    return fl;
  }

  private Future<Void> handleFilter(IQueryRambler rambler, Object filter) {
    if (!(filter instanceof IRamblerSource)) {
      return Future.failedFuture(
          new UnsupportedOperationException("NOT AN INSTANCE OF IRamblerSource: " + filter.getClass().getName()));
    }
    Future<Void> f = Future.future();
    ((IRamblerSource) filter).applyTo(rambler, f.completer());
    return f;
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

  @SuppressWarnings("rawtypes")
  @Override
  public IQueryContainer close() {
    Object filter = filters.get(filters.size() - 1);
    if (filter instanceof IFieldParameter)
      ((IFieldParameter) filter).setCloseParenthesis(true);
    else
      throw new UnsupportedOperationException(
          "closing should be possible only after fields? Current instance is of class " + filter.getClass().getName());
    return this;
  }

}
