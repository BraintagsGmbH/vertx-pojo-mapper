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

import com.mongodb.MongoException;

import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.exception.WriteException;
import de.braintags.vertx.jomnigate.json.dataaccess.JsonStoreObject;
import de.braintags.vertx.jomnigate.json.dataaccess.JsonWrite;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mongo.MongoDataStore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

/**
 * An implementation of {@link IWrite} for Mongo
 * 
 * @author Michael Remme
 * @param <T>
 *          the type of the underlaying mapper
 */
public class MongoWrite<T> extends JsonWrite<T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MongoWrite.class);

  /**
   * Constructor
   * 
   * @param mapperClass
   *          the mapper class
   * @param datastore
   *          the datastore to be used
   */
  public MongoWrite(final Class<T> mapperClass, MongoDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  protected void doInsert(T entity, JsonStoreObject<T> storeObject, Handler<AsyncResult<Object>> resultHandler) {
    MongoClient mongoClient = (MongoClient) ((MongoDataStore) getDataStore()).getClient();
    IMapper<T> mapper = getMapper();
    String collection = mapper.getTableInfo().getName();
    mongoClient.insert(collection, storeObject.getContainer(), result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        resultHandler.handle(Future.succeededFuture(result.result()));
      }
    });
  }

  @Override
  protected void doUpdate(T entity, JsonStoreObject<T> storeObject, Handler<AsyncResult<Object>> resultHandler) {
    MongoClient mongoClient = (MongoClient) ((MongoDataStore) getDataStore()).getClient();
    IMapper<T> mapper = getMapper();
    String collection = mapper.getTableInfo().getName();
    final Object currentId = storeObject.get(mapper.getIdInfo().getField());

    mongoClient.save(collection, storeObject.getContainer(), result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(new WriteException(result.cause())));
      } else {
        LOGGER.debug("updated");
        resultHandler.handle(Future.succeededFuture(currentId));
      }
    });
  }

  @Override
  protected boolean isDuplicateKeyException(Throwable t) {
    if (t instanceof MongoException && ((MongoException) t).getCode() == 11000) {
      MongoException mongoException = (MongoException) t;
      // duplicate key can mean any index with unique constraint, not just ID
      if (mongoException.getMessage().indexOf("_id_") >= 0) {
        return true;
      }
    }
    return false;
  }

}
