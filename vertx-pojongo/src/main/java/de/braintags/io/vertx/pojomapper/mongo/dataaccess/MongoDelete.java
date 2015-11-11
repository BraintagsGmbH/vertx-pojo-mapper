/*
 * #%L
 * vertx-pojo-mapper-common
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

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.impl.Delete;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.impl.DeleteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.mongo.MongoClient;

/**
 * An implementation of {@link IDelete} for Mongo
 * 
 * @author Michael Remme
 * @param <T>
 *          the type of the underlaying mapper
 */
public class MongoDelete<T> extends Delete<T> {

  /**
   * Constructor
   * 
   * @param mapperClass
   *          the mapper class
   * @param datastore
   *          the datastore to be used
   */
  public MongoDelete(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  protected void deleteQuery(IQuery<T> q, Handler<AsyncResult<IDeleteResult>> resultHandler) {
    MongoClient mongoClient = ((MongoDataStore) getDataStore()).getMongoClient();
    String collection = getMapper().getTableInfo().getName();
    MongoQuery<T> query = (MongoQuery<T>) q;
    query.createQueryDefinition(qDefResult -> {
      if (qDefResult.failed()) {
        resultHandler.handle(Future.failedFuture(qDefResult.cause()));
      } else {
        mongoClient.remove(collection,
            ((MongoQueryExpression) qDefResult.result().getQueryExpression()).getQueryDefinition(), deleteHandler -> {
          if (deleteHandler.failed()) {
            resultHandler.handle(Future.failedFuture(deleteHandler.cause()));
          } else {
            DeleteResult deleteResult = new MongoDeleteResult(getDataStore(), getMapper(), qDefResult.result());
            resultHandler.handle(Future.succeededFuture(deleteResult));
          }
        });
      }
    });
  }

}
