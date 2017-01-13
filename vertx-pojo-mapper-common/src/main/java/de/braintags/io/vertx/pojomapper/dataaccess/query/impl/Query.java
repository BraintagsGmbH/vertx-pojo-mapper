/*-
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
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import java.util.Arrays;
import java.util.Collection;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.impl.AbstractDataAccessObject;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISearchCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISearchConditionContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoPoint;
import de.braintags.io.vertx.pojomapper.datatypes.geojson.Position;
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

  private ISearchCondition searchCondition;
  private int limit = 500;
  private int start = 0;
  private boolean returnCompleteCount = false;
  private SortDefinition<T> sortDefs = new SortDefinition<>(this);
  private Object nativeCommand;

  /**
   * @param mapperClass
   * @param datastore
   */
  public Query(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#execute(io.vertx.core.Handler)
   */
  @Override
  public final void execute(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    sync(syncResult -> {
      if (syncResult.failed()) {
        resultHandler.handle(Future.failedFuture(syncResult.cause()));
      } else {
        buildQueryExpression(result -> {
          if (result.failed()) {
            resultHandler.handle(Future.failedFuture(result.cause()));
          } else {
            IQueryExpression queryExpression = result.result();
            try {
              internalExecute(queryExpression, resultHandler);
            } catch (Exception e) {
              resultHandler.handle(Future.failedFuture(e));
            }
          }
        });
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#buildQueryExpression(io.vertx.core.Handler)
   */
  @Override
  public void buildQueryExpression(Handler<AsyncResult<IQueryExpression>> resultHandler) {
    try {
      IQueryExpression expression = getQueryExpressionClass().newInstance();
      expression.setMapper(getMapper());
      expression.setLimit(getLimit(), getStart());
      if (getNativeCommand() != null)
        expression.setNativeCommand(getNativeCommand());
      if (getSortDefinitions() != null && !getSortDefinitions().isEmpty()) {
        expression.addSort(getSortDefinitions());
      }
      if (getSearchCondition() != null) {
        expression.buildSearchCondition(getSearchCondition(), result -> {
          if (result.failed())
            resultHandler.handle(Future.failedFuture(result.cause()));
          else
            resultHandler.handle(Future.succeededFuture(expression));
        });
      } else {
        resultHandler.handle(Future.succeededFuture(expression));
      }
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  /**
   * @return the implementation of the {@link IQueryExpression} for the current datastore
   */
  protected abstract Class<? extends IQueryExpression> getQueryExpressionClass();

  /**
   * This method is called after the sync call to execute the query
   * 
   * @param queryExpression
   * 
   * @param resultHandler
   */
  protected abstract void internalExecute(IQueryExpression queryExpression,
      Handler<AsyncResult<IQueryResult<T>>> resultHandler);

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#executeCount(io.vertx.core.Handler)
   */
  @Override
  public void executeCount(Handler<AsyncResult<IQueryCountResult>> resultHandler) {
    sync(syncResult -> {
      if (syncResult.failed()) {
        resultHandler.handle(Future.failedFuture(syncResult.cause()));
      } else {
        buildQueryExpression(result -> {
          if (result.failed()) {
            resultHandler.handle(Future.failedFuture(result.cause()));
          } else {
            IQueryExpression queryExpression = result.result();
            try {
              internalExecuteCount(queryExpression, resultHandler);
            } catch (Exception e) {
              LOGGER.debug("error occured", e);
              resultHandler.handle(Future.failedFuture(e));
            }
          }
        });
      }
    });
  }

  /**
   * This method is called after the sync call to execute count the query
   * 
   * @param queryExpression
   * 
   * @param resultHandler
   */
  protected abstract void internalExecuteCount(IQueryExpression queryExpression,
      Handler<AsyncResult<IQueryCountResult>> resultHandler);

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
   * Get the limit for the query
   * 
   * @return the limit
   */
  public final int getLimit() {
    return limit;
  }

  /**
   * Get the start position of the query
   * 
   * @return the start
   */
  public final int getStart() {
    return start;
  }

  /**
   * 
   * @return if the complete number of results should be computed
   */
  public final boolean isReturnCompleteCount() {
    return returnCompleteCount;
  }

  /**
   * @param returnCompleteCount
   *          if the complete number of results should be computed
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
  public ISortDefinition<T> addSort(String fieldName) {
    return addSort(fieldName, true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#addSort(java.lang.String, boolean)
   */
  @Override
  public ISortDefinition<T> addSort(String fieldName, boolean ascending) {
    return sortDefs.addSort(fieldName, ascending);
  }

  /**
   * Get the sort definitions for the current instance
   * 
   * @return a list of {@link SortDefinition}
   */
  public ISortDefinition<T> getSortDefinitions() {
    return sortDefs;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#addNativeCommand(java.lang.Object)
   */
  @Override
  public void setNativeCommand(Object command) {
    this.nativeCommand = command;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#getNativeCommand()
   */
  @Override
  public Object getNativeCommand() {
    return nativeCommand;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#hasQueryArguments()
   */
  @Override
  public boolean hasQueryArguments() {
    return searchCondition != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#setSearchCondition(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.ISearchCondition)
   */
  @Override
  public void setSearchCondition(ISearchCondition searchCondition) {
    this.searchCondition = searchCondition;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#getSearchCondition()
   */
  @Override
  public ISearchCondition getSearchCondition() {
    return searchCondition;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#isEqual(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition isEqual(String field, Object value) {
    return new FieldCondition(field, QueryOperator.EQUALS, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#notEqual(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition notEqual(String field, Object value) {
    return new FieldCondition(field, QueryOperator.NOT_EQUALS, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#larger(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition larger(String field, Object value) {
    return new FieldCondition(field, QueryOperator.LARGER, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#largerOrEqual(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition largerOrEqual(String field, Object value) {
    return new FieldCondition(field, QueryOperator.LARGER_EQUAL, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#smaller(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition smaller(String field, Object value) {
    return new FieldCondition(field, QueryOperator.SMALLER, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#smallerOrEqual(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition smallerOrEqual(String field, Object value) {
    return new FieldCondition(field, QueryOperator.SMALLER_EQUAL, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#in(java.lang.String, java.lang.Object[])
   */
  @Override
  public IFieldCondition in(String field, Object... values) {
    return in(field, Arrays.asList(values));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#in(java.lang.String, java.util.Collection)
   */
  @Override
  public IFieldCondition in(String field, Collection<?> values) {
    return new FieldCondition(field, QueryOperator.IN, values);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#notIn(java.lang.String, java.lang.Object[])
   */
  @Override
  public IFieldCondition notIn(String field, Object... values) {
    return notIn(field, Arrays.asList(values));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#notIn(java.lang.String, java.util.Collection)
   */
  @Override
  public IFieldCondition notIn(String field, Collection<?> values) {
    return new FieldCondition(field, QueryOperator.NOT_IN, values);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#startsWith(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition startsWith(String field, Object value) {
    return new FieldCondition(field, QueryOperator.STARTS, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#endsWith(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition endsWith(String field, Object value) {
    return new FieldCondition(field, QueryOperator.ENDS, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#contains(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition contains(String field, Object value) {
    return new FieldCondition(field, QueryOperator.CONTAINS, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#near(java.lang.String, double, double, int)
   */
  @Override
  public IFieldCondition near(String field, double x, double y, int maxDistance) {
    return new FieldCondition(field, QueryOperator.NEAR,
        new GeoSearchArgument(new GeoPoint(new Position(x, y, new double[0])), maxDistance));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#and(de.braintags.io.vertx.pojomapper.dataaccess.query.
   * ISearchCondition[])
   */
  @Override
  public ISearchConditionContainer and(ISearchCondition... searchConditions) {
    return new QueryAnd(searchConditions);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#or(de.braintags.io.vertx.pojomapper.dataaccess.query.
   * ISearchCondition[])
   */
  @Override
  public ISearchConditionContainer or(ISearchCondition... searchConditions) {
    return new QueryOr(searchConditions);
  }
}
