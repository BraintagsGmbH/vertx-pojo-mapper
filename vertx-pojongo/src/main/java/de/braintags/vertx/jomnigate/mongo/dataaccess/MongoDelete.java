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

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDeleteResult;
import de.braintags.vertx.jomnigate.dataaccess.delete.impl.Delete;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientDeleteResult;

/**
 * An implementation of {@link IDelete} for Mongo
 *
 * @author Michael Remme
 * @param <T>
 *          the type of the underlaying mapper
 */
public class MongoDelete<T> extends Delete<T> implements MongoDataAccesObject<T> {

  /**
   * Constructor
   *
   * @param mapperClass
   *          the mapper class
   * @param datastore
   *          the datastore to be used
   */
  public MongoDelete(final Class<T> mapperClass, final IDataStore datastore) {
    super(mapperClass, datastore);
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.dataaccess.delete.impl.Delete#deleteQuery(de.braintags.vertx.jomnigate.
   * dataaccess.query.IQuery, io.vertx.core.Handler)
   */
  @Override
  protected void deleteQuery(final IQuery<T> q, final Handler<AsyncResult<IDeleteResult>> resultHandler) {
    q.buildQueryExpression(null, qDefResult -> {
      if (qDefResult.failed()) {
        resultHandler.handle(Future.failedFuture(qDefResult.cause()));
      } else {
        removeDocuments(((MongoQueryExpression) qDefResult.result()).getQueryDefinition(), START_TRY_COUNT)
            .<IDeleteResult> map(result -> new MongoDeleteResult(getDataStore(), getMapper(), result))
            .setHandler(resultHandler);
      }
    });
  }

  private Future<MongoClientDeleteResult> removeDocuments(final JsonObject queryExpression, final int tryCount) {
    Future<MongoClientDeleteResult> f = Future.future();
    getMongoClient().removeDocuments(getCollection(), queryExpression, f);
    return f.recover(retryMethod(tryCount, count -> removeDocuments(queryExpression, count)));
  }

}
