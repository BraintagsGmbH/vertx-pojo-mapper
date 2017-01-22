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
import io.vertx.ext.mongo.MongoClient;

/**
 * An implementation of {@link IQuery} for Mongo
 *
 * @author Michael Remme
 * @param <T>
 *          the type of the underlaying mapper
 */
public class MongoQuery<T> extends Query<T> {

  /**
   * Constructor
   *
   * @param mapperClass
   *          the mapper class
   * @param datastore
   *          the datastore to be used
   */
  public MongoQuery(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.query.impl.Query#internalExecute(de.braintags.vertx.jomnigate.
   * dataaccess.query.impl.IQueryExpression, io.vertx.core.Handler)
   */
  @Override
  public void internalExecute(IQueryExpression queryExpression, Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
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
  public void executeExplain(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
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
  public void internalExecuteCount(IQueryExpression queryExpression,
      Handler<AsyncResult<IQueryCountResult>> resultHandler) {
    try {
      doFindCount(queryExpression, resultHandler);
    } catch (Exception e) {
      Future<IQueryCountResult> future = Future.failedFuture(e);
      resultHandler.handle(future);
    }
  }

  private void doFindCount(IQueryExpression queryExpression, Handler<AsyncResult<IQueryCountResult>> resultHandler) {
    MongoClient mongoClient = (MongoClient) ((MongoDataStore) getDataStore()).getClient();
    String column = getMapper().getTableInfo().getName();
    mongoClient.count(column, ((MongoQueryExpression) queryExpression).getQueryDefinition(), qResult -> {
      if (qResult.failed()) {
        Future<IQueryCountResult> future = Future.failedFuture(qResult.cause());
        resultHandler.handle(future);
      } else {
        QueryCountResult qcr = new QueryCountResult(getMapper(), getDataStore(), qResult.result(), queryExpression);
        Future<IQueryCountResult> future = Future.succeededFuture(qcr);
        resultHandler.handle(future);
      }
    });
  }

  private void doFind(MongoQueryExpression queryExpression, Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    MongoClient mongoClient = (MongoClient) ((MongoDataStore) getDataStore()).getClient();
    String column = getMapper().getTableInfo().getName();
    mongoClient.findWithOptions(column, queryExpression.getQueryDefinition(), queryExpression.getFindOptions(),
        qResult -> {
          if (qResult.failed()) {
            Future<IQueryResult<T>> future = Future.failedFuture(new QueryException(queryExpression, qResult.cause()));
            resultHandler.handle(future);
          } else {
            createQueryResult(qResult.result(), queryExpression, resultHandler);
          }
        });
  }

  private void createQueryResult(List<JsonObject> findList, MongoQueryExpression queryExpression,
      Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    MongoQueryResult<T> qR = new MongoQueryResult<>(findList, (MongoDataStore) getDataStore(),
        (MongoMapper) getMapper(), queryExpression);
    if (isReturnCompleteCount()) {
      if (getStart() == 0 && getLimit() > 0 && qR.size() < getLimit()) {
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

  private void fetchCompleteCount(MongoQueryResult<T> qR, Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    int bLimit = getLimit();
    int bStart = getStart();
    setLimit(0);
    setStart(0);
    executeCount(cr -> {
      if (cr.failed()) {
        resultHandler.handle(Future.failedFuture(cr.cause()));
      } else {
        long count = cr.result().getCount();
        qR.setCompleteResult(count);
        setLimit(bLimit);
        setStart(bStart);
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
