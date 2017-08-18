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

import java.util.ArrayList;
import java.util.List;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.impl.AbstractDataAccessObject;
import de.braintags.vertx.jomnigate.dataaccess.query.IFieldValueResolver;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryCountResult;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.ISortDefinition;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
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
  private final SortDefinition<T> sortDefs = new SortDefinition<>();
  private List<String> useFields;
  private Object nativeCommand;

  /**
   * @param mapperClass
   * @param datastore
   */
  public Query(final Class<T> mapperClass, final IDataStore datastore) {
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
  public final void execute(final Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    execute(null, getDataStore().getDefaultQueryLimit(), 0, resultHandler);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#execute(io.vertx.core.Handler)
   */
  @Override
  public final void execute(final IFieldValueResolver resolver, final int limit, final int offset,
      final Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    sync(syncResult -> {
      if (syncResult.failed()) {
        resultHandler.handle(Future.failedFuture(syncResult.cause()));
      } else {
        try {
          Future<IQueryResult<T>> rf = Future.future();
          rf.setHandler(resultHandler);
          IObserverContext context = IObserverContext.createInstance();
          preQuery(context).compose(pre -> executeQuery(resolver, limit, offset))
              .compose(wr -> postQuery(wr, context, rf), rf);
        } catch (Exception e) {
          resultHandler.handle(Future.failedFuture(e));
        }
      }
    });
  }

  private final Future<IQueryResult<T>> executeQuery(final IFieldValueResolver resolver, final int limit,
      final int offset) {
    Future<IQueryResult<T>> f = Future.future();
    buildQueryExpression(resolver, result -> {
      if (result.failed()) {
        f.fail(result.cause());
      } else {
        IQueryExpression queryExpression = result.result();
        queryExpression.setLimit(limit, offset);
        internalExecute(queryExpression, f);
      }
    });
    return f;
  }

  /**
   * Execution done before instances are stored into the datastore
   * 
   * @param context
   * @return
   */
  protected Future<Void> preQuery(final IObserverContext context) {
    return getMapper().getObserverHandler().handleBeforeLoad(this, context);
  }

  /**
   * Execution done after entities were stored into the datastore
   * 
   * @param qr
   * @param context
   * @param nextFuture
   */
  protected void postQuery(final IQueryResult<T> qr, final IObserverContext context,
      final Future<IQueryResult<T>> nextFuture) {
    Future<Void> f = getMapper().getObserverHandler().handleAfterLoad(this, qr, context);
    f.setHandler(res -> {
      if (f.failed()) {
        nextFuture.fail(f.cause());
      } else {
        nextFuture.complete(qr);
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
  public final void executeCount(final Handler<AsyncResult<IQueryCountResult>> resultHandler) {
    executeCount(null, resultHandler);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#buildQueryExpression(io.vertx.core.Handler)
   */
  @Override
  public void executeCount(final IFieldValueResolver resolver,
      final Handler<AsyncResult<IQueryCountResult>> resultHandler) {
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
  public void buildQueryExpression(final IFieldValueResolver resolver,
      final Handler<AsyncResult<IQueryExpression>> resultHandler) {
    try {
      IQueryExpression expression = getQueryExpressionClass().newInstance();
      expression.setMapper(getMapper());
      if (getNativeCommand() != null)
        expression.setNativeCommand(getNativeCommand());
      if (getSortDefinitions() != null && !getSortDefinitions().isEmpty()) {
        expression.addSort(getSortDefinitions());
      }
      expression.setUseFields(getUseFields());
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
  public final IQuery<T> setReturnCompleteCount(final boolean returnCompleteCount) {
    this.returnCompleteCount = returnCompleteCount;
    return this;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#setOrderBy(java.lang.String)
   */
  @Override
  public ISortDefinition<T> addSort(final String fieldName) {
    return addSort(fieldName, true);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#addSort(java.lang.String, boolean)
   */
  @Override
  public ISortDefinition<T> addSort(final String fieldName, final boolean ascending) {
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
  public void setNativeCommand(final Object command) {
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
  public void setSearchCondition(final ISearchCondition searchCondition) {
    if (searchCondition != null) {
      searchCondition.validate(getMapper());
    }
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

  @Override
  public void addUseField(final String fieldName) {
    getUseFields().add(fieldName);
  }

  @Override
  public List<String> getUseFields() {
    if (useFields == null)
      useFields = new ArrayList<>();
    return useFields;
  }

  @Override
  public void setUseFields(final List<String> useFields) {
    this.useFields = useFields;
  }
}
