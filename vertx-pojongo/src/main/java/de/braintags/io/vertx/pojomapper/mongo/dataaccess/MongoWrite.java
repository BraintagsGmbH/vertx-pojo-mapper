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
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.exception.WriteException;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.util.CounterObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
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
public class MongoWrite<T> extends AbstractWrite<T> {
  private static final Logger LOG = LoggerFactory.getLogger(MongoWrite.class);

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
  public void internalSave(Handler<AsyncResult<IWriteResult>> resultHandler) {
    MongoWriteResult rr = new MongoWriteResult();
    if (getObjectsToSave().isEmpty()) {
      resultHandler.handle(Future.succeededFuture(rr));
      return;
    }

    CounterObject<IWriteResult> counter = new CounterObject<>(getObjectsToSave().size(), resultHandler);
    for (T entity : getObjectsToSave()) {
      save(entity, rr, result -> {
        if (result.failed()) {
          counter.setThrowable(result.cause());
        } else {
          if (counter.reduce())
            resultHandler.handle(Future.succeededFuture(rr));
        }
      });
      if (counter.isError())
        return;
    }
  }

  private void save(T entity, IWriteResult writeResult, Handler<AsyncResult<Void>> resultHandler) {
    getDataStore().getMapperFactory().getStoreObjectFactory().createStoreObject(getMapper(), entity, result -> {
      if (result.failed()) {
        WriteException we = new WriteException(result.cause());
        LOG.info("failed", we);
        resultHandler.handle(Future.failedFuture(we));
      } else {
        doSave(entity, (MongoStoreObject) result.result(), writeResult, sResult -> {
          if (sResult.failed()) {
            LOG.info("failed", sResult.cause());
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
    LOG.debug("now saving: " + storeObject.toString());
    if (storeObject.isNewInstance()) {
      doInsert(entity, storeObject, writeResult, resultHandler);
    } else {
      doUpdate(entity, storeObject, writeResult, resultHandler);
    }

  }

  private void doInsert(T entity, MongoStoreObject<T> storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    MongoClient mongoClient = (MongoClient) ((MongoDataStore) getDataStore()).getClient();
    IMapper<T> mapper = getMapper();
    String collection = mapper.getTableInfo().getName();
    mongoClient.insert(collection, storeObject.getContainer(), result -> {
      if (result.failed()) {
        handleInsertError(result.cause(), entity, storeObject, writeResult, resultHandler);
      } else {
        Object id = result.result() == null ? storeObject.generatedId : result.result();
        finishInsert(id, entity, storeObject, writeResult, resultHandler);
      }
    });
  }

  private void handleInsertError(Throwable t, T entity, MongoStoreObject<T> storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    if (t.getMessage().indexOf("duplicate key error") >= 0) {
      if (getMapper().getKeyGenerator() != null) {
        LOG.info("duplicate key, regenerating a new key");
        storeObject.getNextId(niResult -> {
          if (niResult.failed()) {
            resultHandler.handle(Future.failedFuture(
                new WriteException("Could not generate new ID after duplicate key error", niResult.cause())));
          } else {
            doInsert(entity, storeObject, writeResult, resultHandler);
          }
        });
      } else {
        resultHandler.handle(Future
            .failedFuture(new WriteException("Duplicate key error on insert, but no KeyGenerator is defined", t)));
      }
    } else {
      resultHandler.handle(Future.failedFuture(new WriteException(t)));
    }
  }

  private void doUpdate(T entity, MongoStoreObject<T> storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    MongoClient mongoClient = (MongoClient) ((MongoDataStore) getDataStore()).getClient();
    IMapper<T> mapper = getMapper();
    String collection = mapper.getTableInfo().getName();
    final Object currentId = storeObject.get(mapper.getIdField());
    String idFieldName = mapper.getIdField().getColumnInfo().getName();
    JsonObject query = new JsonObject();
    query.put(idFieldName, currentId);

    mongoClient.save(collection, storeObject.getContainer(), result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(new WriteException(result.cause())));
      } else {
        LOG.debug("updated");
        finishUpdate(currentId, entity, storeObject, writeResult, resultHandler);
      }
    });
  }

  private void finishInsert(Object id, T entity, MongoStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    setIdValue(id, storeObject, result -> {
      if (result.failed()) {
        resultHandler.handle(result);
        return;
      }
      try {
        executePostSave(entity, lcr -> {
          if (lcr.failed()) {
            resultHandler.handle(lcr);
          } else {
            writeResult.addEntry(storeObject, id, WriteAction.INSERT);
            resultHandler.handle(Future.succeededFuture());
          }
        });
      } catch (Exception e) {
        resultHandler.handle(Future.failedFuture(e));
      }
    });
  }

  private void finishUpdate(Object id, T entity, MongoStoreObject storeObject, IWriteResult writeResult,
      Handler<AsyncResult<Void>> resultHandler) {
    try {
      executePostSave(entity, lcr -> {
        if (lcr.failed()) {
          resultHandler.handle(lcr);
        } else {
          writeResult.addEntry(storeObject, id, WriteAction.UPDATE);
          resultHandler.handle(Future.succeededFuture());
        }
      });
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

}
