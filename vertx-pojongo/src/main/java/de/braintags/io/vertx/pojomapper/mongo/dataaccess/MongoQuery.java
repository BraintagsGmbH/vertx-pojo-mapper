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
package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.Query;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.QueryCountResult;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.mongo.mapper.MongoMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class MongoQuery<T> extends Query<T> {

  /**
   * @param mapperClass
   * @param datastore
   */
  public MongoQuery(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public void execute(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    try {
      createQueryDefinition(result -> {
        if (result.failed()) {
          resultHandler.handle(Future.failedFuture(result.cause()));
        } else {
          doFind(result.result(), resultHandler);
        }
      });
    } catch (Exception e) {
      Future<IQueryResult<T>> future = Future.failedFuture(e);
      resultHandler.handle(future);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#executeExplain(io.vertx.core.Handler)
   */
  @Override
  public void executeExplain(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    resultHandler.handle(Future.failedFuture(new UnsupportedOperationException("Not implemented yet")));
  }

  @Override
  public void executeCount(Handler<AsyncResult<IQueryCountResult>> resultHandler) {
    try {
      createQueryDefinition(result -> {
        if (result.failed()) {
          resultHandler.handle(Future.failedFuture(result.cause()));
        } else {
          doFindCount(result.result(), resultHandler);
        }
      });
    } catch (Exception e) {
      Future<IQueryCountResult> future = Future.failedFuture(e);
      resultHandler.handle(future);
    }
  }

  private void doFindCount(JsonObject query, Handler<AsyncResult<IQueryCountResult>> resultHandler) {
    MongoClient mongoClient = ((MongoDataStore) getDataStore()).getMongoClient();
    String column = getMapper().getTableInfo().getName();
    mongoClient.count(column, query, qResult -> {
      if (qResult.failed()) {
        Future<IQueryCountResult> future = Future.failedFuture(qResult.cause());
        resultHandler.handle(future);
      } else {
        QueryCountResult qcr = new QueryCountResult(getMapper(), getDataStore(), qResult.result(), query);
        Future<IQueryCountResult> future = Future.succeededFuture(qcr);
        resultHandler.handle(future);
      }
    });
  }

  private void doFind(JsonObject query, Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    MongoClient mongoClient = ((MongoDataStore) getDataStore()).getMongoClient();
    String column = getMapper().getTableInfo().getName();
    mongoClient.find(column, query, qResult -> {
      if (qResult.failed()) {
        Future<IQueryResult<T>> future = Future.failedFuture(qResult.cause());
        resultHandler.handle(future);
      } else {
        IQueryResult<T> qR = createQueryResult(qResult.result(), query);
        Future<IQueryResult<T>> future = Future.succeededFuture(qR);
        resultHandler.handle(future);
      }
    });
  }

  private IQueryResult<T> createQueryResult(List<JsonObject> findList, JsonObject query) {
    return new MongoQueryResult<T>(findList, (MongoDataStore) getDataStore(), (MongoMapper) getMapper(), query);
  }

  /**
   * Create the {@link JsonObject} which then contains the definition for the query, which will be executed by
   * {@link MongoClient}
   * 
   * @param resultHandler
   *          the resulthandler which will receive notification
   */
  void createQueryDefinition(Handler<AsyncResult<JsonObject>> resultHandler) {
    MongoQueryRambler rambler = new MongoQueryRambler();
    executeQueryRambler(rambler, result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        resultHandler.handle(Future.succeededFuture(rambler.getJsonObject()));
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#parent()
   */
  @Override
  public Object parent() {
    return null;
  }

}
