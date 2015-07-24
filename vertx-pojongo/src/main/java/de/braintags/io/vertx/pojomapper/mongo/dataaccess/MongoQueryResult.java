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

import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.mongo.mapper.MongoMapper;
import de.braintags.io.vertx.util.AbstractCollectionAsync;
import de.braintags.io.vertx.util.IteratorAsync;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class MongoQueryResult<T> extends AbstractCollectionAsync<T> implements IQueryResult<T> {
  private MongoDataStore store;
  private MongoMapper mapper;
  private JsonObject originalQuery;

  /**
   * Contains the original result from mongo
   */
  private List<JsonObject> jsonResult;
  private T[] pojoResult;

  /**
   * 
   */
  @SuppressWarnings("unchecked")
  public MongoQueryResult(List<JsonObject> jsonResult, MongoDataStore store, MongoMapper mapper,
      JsonObject originalQuery) {
    this.mapper = mapper;
    this.store = store;
    this.jsonResult = jsonResult;
    this.originalQuery = originalQuery;
    pojoResult = (T[]) new Object[jsonResult.size()];
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.CollectionAsync#size()
   */
  @Override
  public int size() {
    return pojoResult.length;
  }

  @SuppressWarnings("unchecked")
  private void generatePojo(int i, Handler<AsyncResult<Void>> handler) {
    JsonObject sourceObject = jsonResult.get(i);
    MongoStoreObject storeObject = new MongoStoreObject(sourceObject, getMapper());
    storeObject.initToEntity(result -> {
      if (result.failed()) {
        handler.handle(result);
      } else {
        pojoResult[i] = (T) storeObject.getEntity();
        handler.handle(Future.succeededFuture());
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult#getDataStore()
   */
  @Override
  public IDataStore getDataStore() {
    return store;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult#getMapper()
   */
  @Override
  public IMapper getMapper() {
    return mapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult#getOriginalQuery()
   */
  @Override
  public Object getOriginalQuery() {
    return originalQuery;
  }

  @Override
  public IteratorAsync<T> iterator() {
    return null;
  }

  class QueryResultIterator implements IteratorAsync<T> {
    private int currentIndex = 0;

    @Override
    public boolean hasNext() {
      return currentIndex < pojoResult.length;
    }

    @Override
    public void next(Handler<AsyncResult<T>> handler) {
      if (pojoResult[currentIndex] == null) {
        generatePojo(currentIndex, result -> {
          if (result.failed()) {
            handler.handle(Future.failedFuture(result.cause()));
          } else {
            handler.handle(Future.succeededFuture(pojoResult[currentIndex++]));
          }
        });
      } else {
        handler.handle(Future.succeededFuture(pojoResult[currentIndex++]));
      }
    }

  }

}
