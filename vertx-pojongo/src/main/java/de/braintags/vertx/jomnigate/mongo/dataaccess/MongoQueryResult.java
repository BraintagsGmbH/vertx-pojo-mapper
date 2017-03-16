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

import de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.AbstractQueryResult;
import de.braintags.vertx.jomnigate.mapping.IStoreObjectFactory;
import de.braintags.vertx.jomnigate.mongo.MongoDataStore;
import de.braintags.vertx.jomnigate.mongo.mapper.MongoMapper;
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
      MongoQueryExpression queryExpression) {
    super(store, mapper, jsonResult.size(), queryExpression);
    this.jsonResult = jsonResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.query.impl.AbstractQueryResult#generatePojo(int,
   * io.vertx.core.Handler)
   */
  @Override
  protected void generatePojo(int i, Handler<AsyncResult<T>> handler) {
    JsonObject sourceObject = jsonResult.get(i);
    IStoreObjectFactory<JsonObject> sf = getDataStore().getStoreObjectFactory();
    sf.createStoreObject(sourceObject, getMapper(), result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        @SuppressWarnings("unchecked")
        T pojo = result.result().getEntity();
        handler.handle(Future.succeededFuture(pojo));
      }
    });
  }

  public List<JsonObject> getOriginalResult() {
    return jsonResult;
  }
}
