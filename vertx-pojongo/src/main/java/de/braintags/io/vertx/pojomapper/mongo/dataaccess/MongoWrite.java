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

import de.braintags.io.vertx.pojomapper.dataaccess.impl.AbstractWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.dataaccess.write.impl.WriteResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

/**
 * @author Michael Remme
 * @param <T>
 */

public class MongoWrite<T> extends AbstractWrite<T> {
  private static Logger logger = LoggerFactory.getLogger(MongoWrite.class);

  /**
   * 
   */
  public MongoWrite(final Class<T> mapperClass, MongoDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public void save(Handler<AsyncResult<IWriteResult>> resultHandler) {
    WriteResult rr = new WriteResult();
    if (getObjectsToSave().isEmpty()) {
      resultHandler.handle(Future.succeededFuture(rr));
      return;
    }

    ErrorObject<IWriteResult> ro = new ErrorObject<IWriteResult>(resultHandler);
    CounterObject counter = new CounterObject(getObjectsToSave().size());
    for (T entity : getObjectsToSave()) {
      save(entity, rr, result -> {
        if (result.failed()) {
          ro.setThrowable(result.cause());
        } else {
          if (counter.reduce())
            resultHandler.handle(Future.succeededFuture(rr));
        }
      });
      if (ro.isError())
        return;
    }
  }

  private void save(T entity, IWriteResult writeResult, Handler<AsyncResult<Void>> resultHandler) {
    getDataStore().getStoreObjectFactory().createStoreObject(getMapper(), entity, result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        doSave(entity, (MongoStoreObject) result.result(), writeResult, sResult -> {
          if (sResult.failed()) {
            resultHandler.handle(Future.failedFuture(sResult.cause()));
          } else {
            resultHandler.handle(Future.succeededFuture());
          }
        });
      }
    });
  }

  /**
   * execute the action to store ONE instance in mongo
   * 
   * @param storeObject
   * @param resultHandler
   */
  private void doSave(T entity, MongoStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    MongoClient mongoClient = ((MongoDataStore) getDataStore()).getMongoClient();
    IMapper mapper = getMapper();
    String column = mapper.getTableInfo().getName();
    final String currentId = (String) storeObject.get(mapper.getIdField());
    logger.info("now saving");
    mongoClient.save(column, storeObject.getContainer(), result -> {
      if (result.failed()) {
        logger.info("failed", result.cause());
        Future<Void> future = Future.failedFuture(result.cause());
        resultHandler.handle(future);
        return;
      }

      logger.info("saved");
      String id = result.result();
      if (id == null) {
        finishUpdate(currentId, entity, storeObject, writeResult, resultHandler);
      } else
        finishInsert(id, entity, storeObject, writeResult, resultHandler);
    });

  }

  private void finishInsert(String id, T entity, MongoStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    setIdValue(id, storeObject, result -> {
      if (result.failed()) {
        resultHandler.handle(result);
        return;
      }
      executePostSave(entity);
      writeResult.addEntry(storeObject, id, WriteAction.INSERT);
      resultHandler.handle(Future.succeededFuture());
    });
  }

  private void finishUpdate(String id, T entity, MongoStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    executePostSave(entity);
    writeResult.addEntry(storeObject, id, WriteAction.UPDATE);
    resultHandler.handle(Future.succeededFuture());
  }

}
