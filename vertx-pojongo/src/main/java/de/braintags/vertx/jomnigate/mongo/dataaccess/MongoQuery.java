/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mongo.dataaccess;

import java.util.List;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryCountResult;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.Query;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.QueryCountResult;
import de.braintags.vertx.jomnigate.exception.QueryException;
import de.braintags.vertx.jomnigate.mongo.MongoDataStore;
import de.braintags.vertx.jomnigate.mongo.mapper.MongoMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * An implementation of {@link IQuery} for Mongo
 *
 * @author Michael Remme
 * @param <T>
 *          the type of the underlaying mapper
 */
public class MongoQuery<T> extends Query<T> implements MongoDataAccesObject<T> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(MongoQuery.class);
  private static final String SEARCH_LOG = "executing query in database %s collection %s with %s";

  /**
   * Constructor
   *
   * @param mapperClass
   *          the mapper class
   * @param datastore
   *          the datastore to be used
   */
  public MongoQuery(final Class<T> mapperClass, final IDataStore datastore) {
    super(mapperClass, datastore);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.impl.Query#internalExecute(de.braintags.vertx.jomnigate.
   * dataaccess.query.impl.IQueryExpression, io.vertx.core.Handler)
   */
  @Override
  public void internalExecute(final IQueryExpression queryExpression,
      final Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    try {
      doFind((MongoQueryExpression) queryExpression, resultHandler);
    } catch (Exception e) {
      Future<IQueryResult<T>> future = Future.failedFuture(e);
      resultHandler.handle(future);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#executeExplain(io.vertx.core.Handler)
   */
  @Override
  public void executeExplain(final Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    resultHandler.handle(Future.failedFuture(new UnsupportedOperationException("Not implemented yet")));
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.query.impl.Query#internalExecuteCount(de.braintags.vertx.jomnigate.
   * dataaccess.query.impl.IQueryExpression, io.vertx.core.Handler)
   */
  @Override
  public void internalExecuteCount(final IQueryExpression queryExpression,
      final Handler<AsyncResult<IQueryCountResult>> resultHandler) {
    count(((MongoQueryExpression) queryExpression).getQueryDefinition(), START_TRY_COUNT)
        .<IQueryCountResult> map(
            queryResult -> new QueryCountResult(getMapper(), getDataStore(), queryResult, queryExpression))
        .setHandler(resultHandler);
  }

  private Future<Long> count(final JsonObject queryDefinition, final int tryCount) {
    Future<Long> f = Future.future();
    getMongoClient().count(getCollection(), queryDefinition, f);
    return f.recover(retryMethod(tryCount, count -> count(queryDefinition, count)));
  }

  private void doFind(final MongoQueryExpression queryExpression,
      final Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format(SEARCH_LOG, getDataStore().getSettings().getDatabaseName(), getCollection(),
          queryExpression.getQueryDefinition()));

    find(queryExpression, 1).setHandler(res -> {
      if (res.failed()) {
        resultHandler.handle(Future.failedFuture(new QueryException(queryExpression, res.cause())));
      } else {
        createQueryResult(res.result(), queryExpression, resultHandler);
      }
    });
  }

  private Future<List<JsonObject>> find(final MongoQueryExpression queryExpression, final int tryCount) {
    Future<List<JsonObject>> f = Future.future();
    getMongoClient().findWithOptions(getCollection(), queryExpression.getQueryDefinition(),
        queryExpression.getFindOptions(), f);
    return f.recover(retryMethod(tryCount, count -> find(queryExpression, count)));
  }

  private void createQueryResult(final List<JsonObject> findList, final MongoQueryExpression queryExpression,
      final Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    MongoQueryResult<T> qR = new MongoQueryResult<>(findList, (MongoDataStore) getDataStore(),
        (MongoMapper) getMapper(), queryExpression);
    if (isReturnCompleteCount()) {
      if (queryExpression.getOffset() == 0 && queryExpression.getLimit() > 0
          && qR.size() < queryExpression.getLimit()) {
        qR.setCompleteResult(qR.size());
        resultHandler.handle(Future.succeededFuture(qR));
      } else {
        fetchCompleteCount(qR, resultHandler);
      }
    } else {
      qR.setCompleteResult(-1);
      resultHandler.handle(Future.succeededFuture(qR));
    }
  }

  private void fetchCompleteCount(final MongoQueryResult<T> qR,
      final Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    executeCount(cr -> {
      if (cr.failed()) {
        resultHandler.handle(Future.failedFuture(cr.cause()));
      } else {
        long count = cr.result().getCount();
        qR.setCompleteResult(count);
        resultHandler.handle(Future.succeededFuture(qR));
      }
    });
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.query.impl.Query#getQueryExpressionClass()
   */
  @Override
  protected Class<? extends IQueryExpression> getQueryExpressionClass() {
    return MongoQueryExpression.class;
  }

}
