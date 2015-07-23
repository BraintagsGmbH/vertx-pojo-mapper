/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.Query;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.mongo.mapper.MongoMapper;

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
    } catch (Throwable e) {
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
  }

  @Override
  public void executeCount(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    throw new UnsupportedOperationException();
  }

  private void doFind(JsonObject query, Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    MongoClient mongoClient = ((MongoDataStore) getDataStore()).getMongoClient();
    String column = getMapper().getDataStoreName();
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

  private void createQueryDefinition(Handler<AsyncResult<JsonObject>> resultHandler) {
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
