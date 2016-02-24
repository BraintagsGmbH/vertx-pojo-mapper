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

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.impl.AbstractDataAccessObject;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IRamblerSource;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;
import de.braintags.io.vertx.util.CounterObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * An abstract implementation of {@link IQuery}
 * 
 * @author Michael Remme
 * @param <T>
 *          the underlaying mapper to be used
 */

public abstract class Query<T> extends AbstractDataAccessObject<T> implements IQuery<T> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory.getLogger(Query.class);

  private List<Object> filters = new ArrayList<Object>();
  private int limit = 500;
  private int start = 0;
  private boolean returnCompleteCount = false;
  private List<SortDefinition> sortDefs = new ArrayList();

  /**
   * @param mapperClass
   * @param datastore
   */
  public Query(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public IFieldParameter<Query<T>> field(String fieldName) {
    FieldParameter<Query<T>> param = new FieldParameter<Query<T>>(this, getMapper().getField(fieldName));
    filters.add(param);
    return param;
  }

  @Override
  public IFieldParameter<LogicContainer<Query<T>>> and(String fieldName) {
    LogicContainer<Query<T>> container = new LogicContainer<Query<T>>(this, QueryLogic.AND);
    filters.add(container);
    return container.field(fieldName);
  }

  @Override
  public IFieldParameter<? extends ILogicContainer<? extends IQueryContainer>> andOpen(String fieldName) {
    LogicContainer<IQueryContainer> container = new LogicContainer<IQueryContainer>(this, QueryLogic.AND_OPEN);
    filters.add(container);
    return container.field(fieldName);
  }

  @Override
  public IFieldParameter<LogicContainer<Query<T>>> or(String fieldName) {
    LogicContainer<Query<T>> container = new LogicContainer<Query<T>>(this, QueryLogic.OR);
    filters.add(container);
    return container.field(fieldName);
  }

  @Override
  public IFieldParameter<? extends ILogicContainer<? extends IQueryContainer>> orOpen(String fieldName) {
    LogicContainer<IQueryContainer> container = new LogicContainer<IQueryContainer>(this, QueryLogic.OR_OPEN);
    filters.add(container);
    return container.field(fieldName);
  }

  @Override
  public IQueryContainer close() {
    LOGGER.warn("Closing on IQuery makes no sense");
    return this;
  }

  /**
   * Traverses through all elements of the current definition, which implement {@link IRamblerSource} and executes the
   * methods of the {@link IQueryRambler}
   * 
   * @param rambler
   *          the rambler to be filled
   * @param resulthandler
   *          the handler to be informed about the result
   */
  public void executeQueryRambler(IQueryRambler rambler, Handler<AsyncResult<Void>> resultHandler) {
    rambler.start(this);
    if (filters.isEmpty()) {
      finishCounter(rambler, resultHandler);
      return;
    }
    CounterObject<Void> co = new CounterObject<Void>(filters.size(), resultHandler);
    for (Object filter : filters) {
      handleFilter(rambler, resultHandler, filter, co);
      if (co.isError()) {
        return;
      }
    }
  }

  private void handleFilter(IQueryRambler rambler, Handler<AsyncResult<Void>> resultHandler, Object filter,
      CounterObject<Void> co) {
    if (!(filter instanceof IRamblerSource)) {
      co.setThrowable(
          new UnsupportedOperationException("NOT AN INSTANCE OF IRamblerSource: " + filter.getClass().getName()));
      return;
    }

    ((IRamblerSource) filter).applyTo(rambler, result -> {
      if (result.failed()) {
        co.setThrowable(result.cause());
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

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#setLimit(int)
   */
  @Override
  public IQuery<T> setLimit(int limit) {
    this.limit = limit;
    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#setStart(int)
   */
  @Override
  public IQuery<T> setStart(int start) {
    this.start = start;
    return this;
  }

  /**
   * @return the limit
   */
  public final int getLimit() {
    return limit;
  }

  /**
   * @return the start
   */
  public final int getStart() {
    return start;
  }

  /**
   * @return the returnCompleteCount
   */
  public final boolean isReturnCompleteCount() {
    return returnCompleteCount;
  }

  /**
   * @param returnCompleteCount
   *          the returnCompleteCount to set
   */
  @Override
  public final IQuery<T> setReturnCompleteCount(boolean returnCompleteCount) {
    this.returnCompleteCount = returnCompleteCount;
    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#setOrderBy(java.lang.String)
   */
  @Override
  public IQuery<T> addSort(String fieldName) {
    return addSort(fieldName, true);
  }

  @Override
  public IQuery<T> addSort(String fieldName, boolean ascending) {
    sortDefs.add(new SortDefinition(fieldName, ascending));
    return this;
  }

  /**
   * Get the sort definitions for the current instance
   * 
   * @return a list of {@link SortDefinition}
   */
  public List<SortDefinition> getSortDefinitions() {
    return sortDefs;
  }

  public class SortDefinition {
    public String fieldName;
    public boolean ascending;

    SortDefinition(String fieldName, boolean ascending) {
      this.fieldName = fieldName;
      this.ascending = ascending;
    }
  }
}
