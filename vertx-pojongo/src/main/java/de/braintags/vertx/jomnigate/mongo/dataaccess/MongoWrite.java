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

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoException;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteEntry;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.dataaccess.write.WriteAction;
import de.braintags.vertx.jomnigate.dataaccess.write.impl.AbstractWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.impl.WriteEntry;
import de.braintags.vertx.jomnigate.exception.DuplicateKeyException;
import de.braintags.vertx.jomnigate.exception.WriteException;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import de.braintags.vertx.jomnigate.mongo.MongoDataStore;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoClientUpdateResult;

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
  public MongoWrite(final Class<T> mapperClass, final MongoDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public Future<IWriteResult> internalSave(final IObserverContext context) {
    Future<IWriteResult> f = Future.future();
    if (getObjectsToSave().isEmpty()) {
      f.complete(new MongoWriteResult());
    } else {
      CompositeFuture cf = saveRecords(context);
      cf.setHandler(cfr -> {
        if (cfr.failed()) {
          f.fail(cfr.cause());
        } else {
          f.complete(new MongoWriteResult(cf.list()));
        }
      });
    }
    return f;
  }

  @SuppressWarnings("rawtypes")
  private CompositeFuture saveRecords(final IObserverContext context) {
    List<Future> fl = new ArrayList<>(getObjectsToSave().size());
    for (T entity : getObjectsToSave()) {
      fl.add(save(entity, context));
    }
    return CompositeFuture.all(fl);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Future<IWriteEntry> save(final T entity, final IObserverContext context) {
    // TODO refactoring / abstraction will follow when refactoring MySql
    Future<IWriteEntry> f = Future.future();
    preSave(entity, context).compose(r1 -> createStoreObject(entity))
        .compose(sto -> doSave(entity, (MongoStoreObject) sto, f), f);
    return f;
  }

  private Future<IStoreObject<T, ?>> createStoreObject(final T entity) {
    Future<IStoreObject<T, ?>> f = Future.future();
    getDataStore().getStoreObjectFactory().createStoreObject(getMapper(), entity, f);
    return f;
  }

  /**
   * Execution done before instances are stored into the datastore
   * 
   * @return
   */
  protected Future<Void> preSave(final T entity, final IObserverContext context) {
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
  private boolean isNewInstance(final T entity) {
    Object javaValue = getMapper().getIdInfo().getField().getPropertyAccessor().readData(entity);
    return javaValue == null;
  }

  /**
   * execute the action to store ONE instance in mongo
   * 
   * @param storeObject
   * @param resultHandler
   */
  private void doSave(final T entity, final MongoStoreObject<T> storeObject, final Future<IWriteEntry> future) {
    LOG.debug("now saving: " + storeObject.toString());
    if (storeObject.isNewInstance()) {
      if (getQuery() != null) {
        throw new IllegalStateException("Can not update with a query and objects without id");
      }
      doInsert(entity, storeObject, future);
    } else {
      doUpdate(entity, storeObject, future);
    }
  }

  private void doInsert(final T entity, final MongoStoreObject<T> storeObject,
      final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    MongoClient mongoClient = (MongoClient) ((MongoDataStore) getDataStore()).getClient();
    IMapper<T> mapper = getMapper();
    String collection = mapper.getTableInfo().getName();
    mongoClient.insert(collection, storeObject.getContainer(), result -> {
      if (result.failed()) {
        handleInsertError(result.cause(), entity, storeObject, resultHandler);
      } else {
        Object id = result.result() == null ? storeObject.getGeneratedId() : result.result();
        finishInsert(id, entity, storeObject, resultHandler);
      }
    });
  }

  /**
   * In case of duplicate key exception, try to generate another one
   * 
   * @param t
   * @param entity
   * @param storeObject
   * @param resultHandler
   */
  private void handleInsertError(final Throwable t, final T entity, final MongoStoreObject<T> storeObject,
      final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    // 11000 is the code for duplicate key error
    if (t instanceof MongoException && ((MongoException) t).getCode() == 11000) {
      MongoException mongoException = (MongoException) t;
      // duplicate key can mean any index with unique constraint, not just ID
      if (mongoException.getMessage().indexOf("_id_") >= 0) {
        if (getMapper().getKeyGenerator() != null) {
          LOG.info("duplicate key, regenerating a new key");
          storeObject.getNextId(niResult -> {
            if (niResult.failed()) {
              resultHandler.handle(Future.failedFuture(
                  new DuplicateKeyException("Could not generate new ID after duplicate key error", niResult.cause())));
            } else {
              doInsert(entity, storeObject, resultHandler);
            }
          });
        } else {
          resultHandler.handle(Future.failedFuture(
              new DuplicateKeyException("Duplicate key error on insert, but no KeyGenerator is defined", t)));
        }
      } else {
        resultHandler.handle(Future.failedFuture(new DuplicateKeyException(t)));
      }
    } else {
      resultHandler.handle(Future.failedFuture(new WriteException(t)));
    }
  }

  private void doUpdate(final T entity, final MongoStoreObject<T> storeObject,
      final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    MongoClient mongoClient = (MongoClient) ((MongoDataStore) getDataStore()).getClient();
    IMapper<T> mapper = getMapper();
    String collection = mapper.getTableInfo().getName();
    final Object currentId = storeObject.get(mapper.getIdInfo().getField());

    if (getQuery() != null) {
      IQuery<T> q = getDataStore().createQuery(getMapperClass());
      q.setSearchCondition(ISearchCondition.and(ISearchCondition.in(mapper.getIdInfo().getIndexedField(), currentId),
          getQuery().getSearchCondition()));
      q.buildQueryExpression(null, queryExpRes -> {
        mongoClient.replaceDocuments(collection, ((MongoQueryExpression) queryExpRes.result()).getQueryDefinition(),
            storeObject.getContainer(), res -> {
              if (res.succeeded()) {
                finishQueryUpdate(currentId, entity, storeObject, res.result(), resultHandler);
              } else {
                resultHandler.handle(Future.failedFuture(new WriteException(res.cause())));
              }
            });
      });
    } else {
      mongoClient.save(collection, storeObject.getContainer(), result -> {
        if (result.failed()) {
          resultHandler.handle(Future.failedFuture(new WriteException(result.cause())));
        } else {
          LOG.debug("updated");
          finishUpdate(currentId, entity, storeObject, resultHandler);
        }
      });
    }

  }

  private void finishQueryUpdate(final Object id, final T entity, final MongoStoreObject<T> storeObject,
      final MongoClientUpdateResult updateResult, final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    if (updateResult.getDocMatched() != 0 && updateResult.getDocMatched() == updateResult.getDocModified()) {
      executePostSave(entity, lcr -> {
        if (lcr.failed()) {
          resultHandler.handle(Future.failedFuture(lcr.cause()));
        } else {
          resultHandler.handle(Future.succeededFuture(new WriteEntry(storeObject, id, WriteAction.UPDATE)));
        }
      });
    } else if (updateResult.getDocMatched() == 0 && updateResult.getDocModified() == 0) {
      resultHandler.handle(Future.succeededFuture(new WriteEntry(storeObject, id, WriteAction.NOT_MATCHED)));
    } else {
      resultHandler.handle(Future.failedFuture(new WriteException("Matched " + updateResult.getDocMatched()
          + "documents but modified: " + updateResult.getDocModified() + "documents")));
    }
  }

  private void finishInsert(final Object id, final T entity, final MongoStoreObject<T> storeObject,
      final Handler<AsyncResult<IWriteEntry>> resultHandler) {
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

  private void finishUpdate(final Object id, final T entity, final MongoStoreObject<T> storeObject,
      final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    executePostSave(entity, lcr -> {
      if (lcr.failed()) {
        resultHandler.handle(Future.failedFuture(lcr.cause()));
      } else {
        resultHandler.handle(Future.succeededFuture(new WriteEntry(storeObject, id, WriteAction.UPDATE)));
      }
    });
  }

}
