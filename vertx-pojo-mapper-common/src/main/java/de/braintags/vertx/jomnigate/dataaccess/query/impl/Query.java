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
package de.braintags.vertx.jomnigate.dataaccess.query.impl;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.impl.AbstractDataAccessObject;
import de.braintags.vertx.jomnigate.dataaccess.query.IFieldCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.IFieldValueResolver;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryCountResult;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchConditionContainer;
import de.braintags.vertx.jomnigate.dataaccess.query.ISortDefinition;
import de.braintags.vertx.jomnigate.dataaccess.query.QueryOperator;
import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;
import de.braintags.vertx.jomnigate.datatypes.geojson.Position;
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
  private boolean returnCompleteCount = false;
  private SortDefinition<T> sortDefs = new SortDefinition<>();
  private Object nativeCommand;

  /**
   * @param mapperClass
   * @param datastore
   */
  public Query(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  /**
   * Execute the query. Any variables in the search condition will result in an error. The used value for limit is the
   * default query limit of the current datastore. The used value for the offset is 0.
   *
   * @param resultHandler
   * @see #execute(IFieldValueResolver, int, int, Handler)
   */
  @Override
  public final void execute(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    execute(null, getDataStore().getDefaultQueryLimit(), 0, resultHandler);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#execute(io.vertx.core.Handler)
   */
  @Override
  public final void execute(IFieldValueResolver resolver, int limit, int offset,
      Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    sync(syncResult -> {
      if (syncResult.failed()) {
        resultHandler.handle(Future.failedFuture(syncResult.cause()));
      } else {
        buildQueryExpression(resolver, result -> {
          if (result.failed()) {
            resultHandler.handle(Future.failedFuture(result.cause()));
          } else {
            IQueryExpression queryExpression = result.result();
            queryExpression.setLimit(limit, offset);
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

  /**
   * Execute the query by counting the fitting objects. Any variables in the search condition will result in an error
   *
   * @param resultHandler
   * @see #executeCount(IFieldValueResolver, Handler)
   */
  @Override
  public final void executeCount(Handler<AsyncResult<IQueryCountResult>> resultHandler) {
    executeCount(null, resultHandler);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#buildQueryExpression(io.vertx.core.Handler)
   */
  @Override
  public void executeCount(IFieldValueResolver resolver, Handler<AsyncResult<IQueryCountResult>> resultHandler) {
    sync(syncResult -> {
      if (syncResult.failed()) {
        resultHandler.handle(Future.failedFuture(syncResult.cause()));
      } else {
        buildQueryExpression(resolver, result -> {
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

  @Override
  public void buildQueryExpression(IFieldValueResolver resolver, Handler<AsyncResult<IQueryExpression>> resultHandler) {
    try {
      IQueryExpression expression = getQueryExpressionClass().newInstance();
      expression.setMapper(getMapper());

      if (getNativeCommand() != null)
        expression.setNativeCommand(getNativeCommand());
      if (getSortDefinitions() != null && !getSortDefinitions().isEmpty()) {
        expression.addSort(getSortDefinitions());
      }
      if (getSearchCondition() != null) {
        expression.buildSearchCondition(getSearchCondition(), resolver, result -> {
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
   * This method is called after the sync call to execute the query
   *
   * @param queryExpression
   *
   * @param resultHandler
   */
  protected abstract void internalExecute(IQueryExpression queryExpression,
      Handler<AsyncResult<IQueryResult<T>>> resultHandler);

  /**
   * This method is called after the sync call to execute count the query
   *
   * @param queryExpression
   *
   * @param resultHandler
   */
  protected abstract void internalExecuteCount(IQueryExpression queryExpression,
      Handler<AsyncResult<IQueryCountResult>> resultHandler);

  /**
   * @return the implementation of the {@link IQueryExpression} for the current datastore
   */
  protected abstract Class<? extends IQueryExpression> getQueryExpressionClass();

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
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#setOrderBy(java.lang.String)
   */
  @Override
  public ISortDefinition<T> addSort(String fieldName) {
    return addSort(fieldName, true);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#addSort(java.lang.String, boolean)
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
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#addNativeCommand(java.lang.Object)
   */
  @Override
  public void setNativeCommand(Object command) {
    this.nativeCommand = command;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#getNativeCommand()
   */
  @Override
  public Object getNativeCommand() {
    return nativeCommand;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#hasQueryArguments()
   */
  @Override
  public boolean hasQueryArguments() {
    return searchCondition != null;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#setSearchCondition(de.braintags.vertx.jomnigate.
   * dataaccess.query.ISearchCondition)
   */
  @Override
  public void setSearchCondition(ISearchCondition searchCondition) {
    this.searchCondition = searchCondition;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#getSearchCondition()
   */
  @Override
  public ISearchCondition getSearchCondition() {
    return searchCondition;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#condition(java.lang.String,
   * de.braintags.vertx.jomnigate.dataaccess.query.QueryOperator, java.lang.Object)
   */
  @Override
  public IFieldCondition condition(String field, QueryOperator operator, Object value) {
    return createFieldCondition(field, operator, value);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#isEqual(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition isEqual(String field, Object value) {
    return createFieldCondition(field, QueryOperator.EQUALS, value);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#notEqual(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition notEqual(String field, Object value) {
    return createFieldCondition(field, QueryOperator.NOT_EQUALS, value);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#larger(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition larger(String field, Object value) {
    return createFieldCondition(field, QueryOperator.LARGER, value);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#largerOrEqual(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition largerOrEqual(String field, Object value) {
    return createFieldCondition(field, QueryOperator.LARGER_EQUAL, value);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#smaller(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition smaller(String field, Object value) {
    return createFieldCondition(field, QueryOperator.SMALLER, value);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#smallerOrEqual(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition smallerOrEqual(String field, Object value) {
    return createFieldCondition(field, QueryOperator.SMALLER_EQUAL, value);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#in(java.lang.String, java.lang.Object[])
   */
  @Override
  public IFieldCondition in(String field, Object... values) {
    return in(field, Arrays.asList(values));
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#in(java.lang.String, java.util.Collection)
   */
  @Override
  public IFieldCondition in(String field, Collection<?> values) {
    return createFieldCondition(field, QueryOperator.IN, values);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#notIn(java.lang.String, java.lang.Object[])
   */
  @Override
  public IFieldCondition notIn(String field, Object... values) {
    return notIn(field, Arrays.asList(values));
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#notIn(java.lang.String, java.util.Collection)
   */
  @Override
  public IFieldCondition notIn(String field, Collection<?> values) {
    return createFieldCondition(field, QueryOperator.NOT_IN, values);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#startsWith(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition startsWith(String field, Object value) {
    return createFieldCondition(field, QueryOperator.STARTS, value);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#endsWith(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition endsWith(String field, Object value) {
    return createFieldCondition(field, QueryOperator.ENDS, value);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#contains(java.lang.String, java.lang.Object)
   */
  @Override
  public IFieldCondition contains(String field, Object value) {
    return createFieldCondition(field, QueryOperator.CONTAINS, value);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#near(java.lang.String, double, double, int)
   */
  @Override
  public IFieldCondition near(String field, double x, double y, int maxDistance) {
    return createFieldCondition(field, QueryOperator.NEAR,
        new GeoSearchArgument(new GeoPoint(new Position(x, y, new double[0])), maxDistance));
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.query.IQuery#and(de.braintags.vertx.jomnigate.dataaccess.query.
   * ISearchCondition[])
   */
  @Override
  public ISearchConditionContainer and(ISearchCondition... searchConditions) {
    return new QueryAnd(searchConditions);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#or(de.braintags.vertx.jomnigate.dataaccess.query.
   * ISearchCondition[])
   */
  @Override
  public ISearchConditionContainer or(ISearchCondition... searchConditions) {
    return new QueryOr(searchConditions);
  }

  /**
   * Create a new field condition object with the given values. Checks if the value is a variable. If yes, creates a
   * {@link VariableFieldCondition} to replace the variable with its actual value during execution.
   *
   * @param field
   *          the field of the condition
   * @param operator
   *          the logic operator for the condition
   * @param value
   *          the value of the condition
   * @return a new field condition object
   */
  private IFieldCondition createFieldCondition(String field, QueryOperator operator, Object value) {
    if (value instanceof String && StringUtils.isNotBlank((String) value)) {
      String stringValue = (String) value;
      if (stringValue.startsWith("${") && stringValue.endsWith("}")) {
        return new VariableFieldCondition(field, operator, stringValue.substring(2, stringValue.length() - 1));
      }
    }
    return new FieldCondition(field, operator, value);
  }
}
