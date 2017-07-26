/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.json.dataaccess;

import java.util.ArrayList;
import java.util.List;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteEntry;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.dataaccess.write.WriteAction;
import de.braintags.vertx.jomnigate.dataaccess.write.impl.AbstractWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.impl.WriteEntry;
import de.braintags.vertx.jomnigate.dataaccess.write.impl.WriteResult;
import de.braintags.vertx.jomnigate.exception.DuplicateKeyException;
import de.braintags.vertx.jomnigate.exception.WriteException;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.mongo.MongoClientUpdateResult;

/**
 * An abstract implementation of IWrite, which uses Json as internal format
 * 
 * @author Michael Remme
 * 
 */
public abstract class JsonWrite<T> extends AbstractWrite<T> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(JsonWrite.class);

  /**
   * @param mapperClass
   * @param datastore
   */
  public JsonWrite(Class mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public Future<IWriteResult> internalSave(IObserverContext context) {
    Future<IWriteResult> f = Future.future();
    if (getObjectsToSave().isEmpty()) {
      f.complete(new WriteResult());
    } else {
      CompositeFuture cf = saveRecords(context);
      cf.setHandler(cfr -> {
        if (cfr.failed()) {
          f.fail(cfr.cause());
        } else {
          f.complete(new WriteResult(cf.list()));
        }
      });
    }
    return f;
  }

  @SuppressWarnings("rawtypes")
  private CompositeFuture saveRecords(IObserverContext context) {
    List<Future> fl = new ArrayList<>(getObjectsToSave().size());
    for (T entity : getObjectsToSave()) {
      fl.add(save(entity, context));
    }
    return CompositeFuture.all(fl);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Future<IWriteEntry> save(T entity, IObserverContext context) {
    Future<IWriteEntry> f = Future.future();
    preSave(entity, context).compose(r1 -> createStoreObject(entity))
        .compose(sto -> doSave(entity, (JsonStoreObject) sto, f), f);
    return f;
  }

  private Future<IStoreObject<T, ?>> createStoreObject(T entity) {
    Future<IStoreObject<T, ?>> f = Future.future();
    getDataStore().getStoreObjectFactory().createStoreObject(getMapper(), entity, f);
    return f;
  }

  /**
   * Execution done before instances are stored into the datastore
   * 
   * @return
   */
  protected Future<Void> preSave(T entity, IObserverContext context) {
    if (isNewInstance(entity)) {
      return getMapper().getObserverHandler().handleBeforeInsert(this, entity, context);
    } else {
      return getMapper().getObserverHandler().handleBeforeUpdate(this, entity, context);
    }
  }

  /**
   * We need the info before the {@link IStoreObject} is created for the event beforeSave
   * 
   * @param entity
   * @return
   */
  private boolean isNewInstance(T entity) {
    Object javaValue = getMapper().getIdInfo().getField().getPropertyAccessor().readData(entity);
    return javaValue == null;
  }

  /**
   * execute the action to store ONE instance in mongo
   * 
   * @param storeObject
   * @param resultHandler
   */
  private void doSave(T entity, JsonStoreObject<T> storeObject, Future<IWriteEntry> future) {
    LOGGER.debug("now saving: " + storeObject.toString());
    if (storeObject.isNewInstance()) {
      _doInsert(entity, storeObject, future);
    } else {
      _doUpdate(entity, storeObject, future);
    }
  }

  protected void _doUpdate(T entity, JsonStoreObject<T> storeObject, Handler<AsyncResult<IWriteEntry>> resultHandler) {
    if (getQuery() != null) {
      doQueryUpdate(entity, storeObject, result -> {
        if (result.failed()) {
          resultHandler.handle(Future.failedFuture(new WriteException(result.cause())));
        } else {
          finishQueryUpdate(result.result(), entity, storeObject, null, resultHandler);
        }
      });
    } else {
      doUpdate(entity, storeObject, result -> {
        if (result.failed()) {
          resultHandler.handle(Future.failedFuture(new WriteException(result.cause())));
        } else {
          finishUpdate(result.result(), entity, storeObject, resultHandler);
        }
      });
    }
  }

  @SuppressWarnings("rawtypes")
  private void finishUpdate(Object id, T entity, JsonStoreObject storeObject,
      Handler<AsyncResult<IWriteEntry>> resultHandler) {
    executePostSave(entity, lcr -> {
      if (lcr.failed()) {
        resultHandler.handle(Future.failedFuture(lcr.cause()));
      } else {
        resultHandler.handle(Future.succeededFuture(new WriteEntry(storeObject, id, WriteAction.UPDATE)));
      }
    });
  }

  private void finishQueryUpdate(final Object id, final T entity, final JsonStoreObject<T> storeObject,
      final MongoClientUpdateResult updateResult, final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    if (updateResult.getDocMatched() != 0 && updateResult.getDocMatched() == updateResult.getDocModified()) {
      finishUpdate(id, entity, storeObject, resultHandler);
    } else if (updateResult.getDocMatched() == 0 && updateResult.getDocModified() == 0) {
      resultHandler.handle(Future.succeededFuture(new WriteEntry(storeObject, id, WriteAction.NOT_MATCHED)));
    } else {
      resultHandler.handle(Future.failedFuture(new WriteException("Matched " + updateResult.getDocMatched()
          + "documents but modified: " + updateResult.getDocModified() + "documents")));
    }
  }

  /**
   * This is the client specific part, where the generated Json is updated into the datastore
   * 
   * @param entity
   * @param storeObject
   * @param resultHandler
   *          the handler, which gets the id of the new record
   */
  protected abstract void doUpdate(T entity, JsonStoreObject<T> storeObject,
      Handler<AsyncResult<Object>> resultHandler);

  /**
   * This is the client specific part, where the generated Json is updated into the datastore for an update based on
   * query
   * 
   * @param entity
   * @param storeObject
   * @param resultHandler
   *          the handler, which gets the id of the new record
   */
  protected abstract void doQueryUpdate(T entity, JsonStoreObject<T> storeObject,
      Handler<AsyncResult<Object>> resultHandler);

  /**
   * perform an insert action. If a duplicate key is reported by an exception, retry the action with a new key
   */
  private void _doInsert(T entity, JsonStoreObject<T> storeObject, Handler<AsyncResult<IWriteEntry>> resultHandler) {
    doInsert(entity, storeObject, result -> {
      if (result.failed()) {
        if (isDuplicateKeyException(result.cause())) {
          nextKey(result.cause(), entity, storeObject, resultHandler);
        } else {
          resultHandler.handle(Future.failedFuture(new WriteException(result.cause())));
        }
      } else {
        Object id = result.result() == null ? storeObject.getGeneratedId() : result.result();
        finishInsert(id, entity, storeObject, resultHandler);
      }
    });
  }

  private void nextKey(Throwable t, T entity, JsonStoreObject<T> storeObject,
      Handler<AsyncResult<IWriteEntry>> resultHandler) {
    if (getMapper().getKeyGenerator() != null) {
      LOGGER.info("duplicate key, regenerating a new key");
      storeObject.getNextId(niResult -> {
        if (niResult.failed()) {
          resultHandler.handle(Future.failedFuture(
              new DuplicateKeyException("Could not generate new ID after duplicate key error", niResult.cause())));
        } else {
          _doInsert(entity, storeObject, resultHandler);
        }
      });
    } else {
      resultHandler.handle(Future
          .failedFuture(new DuplicateKeyException("Duplicate key error on insert, but no KeyGenerator is defined", t)));
    }
  }

  /**
   * This is the client specific part, where the generated Json is inserted into the datastore
   * 
   * @param entity
   * @param storeObject
   * @param resultHandler
   *          the handler, which gets the id of the new record
   */
  protected abstract void doInsert(T entity, JsonStoreObject<T> storeObject,
      Handler<AsyncResult<Object>> resultHandler);

  /**
   * checks, wether the given exception defines a duplicate key
   * 
   * @param e
   * @return
   */
  protected abstract boolean isDuplicateKeyException(Throwable e);

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void finishInsert(Object id, T entity, JsonStoreObject storeObject,
      Handler<AsyncResult<IWriteEntry>> resultHandler) {
    setIdValue(id, storeObject, result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
        return;
      }
      executePostSave(entity, lcr -> {
        if (lcr.failed()) {
          resultHandler.handle(Future.failedFuture(lcr.cause()));
        } else {
          resultHandler.handle(Future.succeededFuture(new WriteEntry(storeObject, id, WriteAction.INSERT)));
        }
      });
    });
  }

}
