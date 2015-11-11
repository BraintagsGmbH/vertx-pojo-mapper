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

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryResult;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.mongo.mapper.MongoMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * An implementation of {@link IQueryResult} for Mongo
 * 
 * @author Michael Remme
 * @param <T>
 *          the type of the underlaying mapper
 */
public class MongoQueryResult<T> extends AbstractQueryResult<T> {
  /**
   * Contains the original result from mongo
   */
  private List<JsonObject> jsonResult;

  /**
   * @param jsonResult
   * @param store
   * @param mapper
   * @param originalQuery
   */
  public MongoQueryResult(List<JsonObject> jsonResult, MongoDataStore store, MongoMapper mapper,
      MongoQueryRambler rambler) {
    super(store, mapper, jsonResult.size(), rambler.getQueryExpression());
    this.jsonResult = jsonResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryResult#generatePojo(int,
   * io.vertx.core.Handler)
   */
  @Override
  protected void generatePojo(int i, Handler<AsyncResult<T>> handler) {
    JsonObject sourceObject = jsonResult.get(i);
    getDataStore().getStoreObjectFactory().createStoreObject(sourceObject, getMapper(), result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        @SuppressWarnings("unchecked")
        T pojo = (T) result.result().getEntity();
        handler.handle(Future.succeededFuture(pojo));
      }
    });
  }

}
