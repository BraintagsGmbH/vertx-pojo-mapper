/*
 * #%L vertx-pojo-mapper-common %% Copyright (C) 2015 Braintags GmbH %% All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html #L%
 */
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.impl.AbstractDataAccessObject;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryPart;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
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

  private IQueryPart rootQueryPart;
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

  public void buildQueryExpression(Handler<AsyncResult<IQueryExpression>> resultHandler) {
    try {
      IQueryExpression expression = getQueryExpressionClass().newInstance();
      expression.setMapper(getMapper());
      expression.setLimit(getLimit(), getStart());
      expression.setNativeCommand(getNativeCommand());
      if (getRootQueryPart() != null) {
        expression.buildQueryExpression(getRootQueryPart(), result -> {
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
  public ISortDefinition<T> addSort(String fieldName) {
    return addSort(fieldName, true);
  }

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
    return rootQueryPart != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#setRootQueryPart(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.IQueryPart)
   */
  @Override
  public void setRootQueryPart(IQueryPart rootQueryPart) {
    this.rootQueryPart = rootQueryPart;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#getRootQueryPart()
   */
  @Override
  public IQueryPart getRootQueryPart() {
    return rootQueryPart;
  }

}
