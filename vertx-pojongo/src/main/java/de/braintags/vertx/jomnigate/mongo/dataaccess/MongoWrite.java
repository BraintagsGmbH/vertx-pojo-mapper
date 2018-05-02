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

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.IntStream;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.bulk.BulkWriteError;

import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression;
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
import de.braintags.vertx.jomnigate.mongo.MongoStoreObjectFactory;
import de.braintags.vertx.jomnigate.mongo.mapper.datastore.MongoColumnInfo;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.BulkOperation.BulkOperationType;
import io.vertx.ext.mongo.MongoClientBulkWriteResult;

/**
 * An implementation of {@link IWrite} for Mongo
 *
 * @author Michael Remme
 * @param <T>
 *          the type of the underlaying mapper
 */
public class MongoWrite<T> extends AbstractWrite<T> implements MongoDataAccesObject<T> {

  protected Class<?> view;
  private JsonObject setOnInsertFields;

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
    List<T> entities = getObjectsToSave();
    if (getQuery() != null && entities.size() > 1)
      f.fail(new IllegalStateException("Can only update one entity at once if a query is defined"));
    else if (entities.isEmpty()) {
      f.complete(new MongoWriteResult());
    } else {
      CompositeFuture.all(entities.stream().map(entity -> convertEntity(entity, context)).collect(toList()))
          .compose(cfConvert -> {
            List<StoreObjectHolder> holders = cfConvert.list();
            return writeEntities(holders).recover(e -> {
              if (entities.size() == 1 && e instanceof MongoBulkWriteException)
                return handleSingleWriteError(holders, (MongoBulkWriteException) e);
              else
                return Future.failedFuture(e);
            });
          }).compose(cfWrite -> {
            f.complete(new MongoWriteResult(cfWrite.list()));
          }, f);
    }
    return f;

  }

  private Future<CompositeFuture> handleSingleWriteError(final List<StoreObjectHolder> holders,
      final MongoBulkWriteException bulkException) {
    if (bulkException.getWriteErrors().size() == 1) {
      BulkWriteError writeError = bulkException.getWriteErrors().get(0);
      if (writeError.getCode() == 11000) {
        if (writeError.getMessage().indexOf("_id_") >= 0) {
          if (getMapper().getKeyGenerator() != null) {
            Future<Void> fNextId = Future.future();
            holders.get(0).storeObject.getNextId(fNextId);
            return fNextId.compose(v -> writeEntities(holders));
          } else {
            return Future.failedFuture(new DuplicateKeyException(
                "Duplicate key error on insert, but no KeyGenerator is defined", bulkException));
          }
        } else {
          return Future.failedFuture(new DuplicateKeyException(bulkException));
        }
      }
    }
    return Future.failedFuture(bulkException);
  }

  private Future<CompositeFuture> writeEntities(final List<StoreObjectHolder> holders) {
    List<BulkOperation> bulkOperations = holders.stream().map(storeObjectHolder -> storeObjectHolder.bulkOperation)
        .collect(toList());
    return write(bulkOperations, START_TRY_COUNT).compose(writeResult -> {
      @SuppressWarnings("rawtypes")
      List<Future> futures = IntStream.range(0, holders.size())
          .mapToObj(i -> finishWrite(getObjectsToSave().get(i), holders.get(i), writeResult)).collect(toList());
      return CompositeFuture.all(futures);
    });
  }

  private Future<MongoClientBulkWriteResult> write(final List<BulkOperation> bulkOperations, final int tryCount) {
    Future<MongoClientBulkWriteResult> fBulk = Future.future();
    getMongoClient().bulkWrite(getCollection(), bulkOperations, fBulk);
    return fBulk.recover(retryMethod(tryCount, count -> write(bulkOperations, count)));
  }

  private Future<IWriteEntry> finishWrite(final T entity, final StoreObjectHolder holder,
      final MongoClientBulkWriteResult writeResult) {
    BulkOperation bulkOperation = holder.bulkOperation;
    MongoStoreObject<T> storeObject = holder.storeObject;
    Future<IWriteEntry> fAfterWrite = Future.future();
    if (bulkOperation.getType() == BulkOperationType.INSERT) {
      Object newId = bulkOperation.getDocument().getString("_id");
      if (newId == null)
        newId = storeObject.getGeneratedId();
      finishInsert(newId, entity, storeObject, fAfterWrite);
    } else {
      Object currentId = storeObject.get(getMapper().getIdInfo().getField());
      if (getQuery() == null)
        finishUpdate(currentId, entity, storeObject, fAfterWrite);
      else
        finishQueryUpdate(currentId, entity, storeObject, writeResult, fAfterWrite);
    }
    return fAfterWrite;
  }

  private Future<StoreObjectHolder> convertEntity(final T entity, final IObserverContext context) {
    return preSave(entity, context).compose(v -> createStoreObject(entity)).compose(this::createBulkOperation);
  }

  private Future<StoreObjectHolder> createBulkOperation(final MongoStoreObject<T> storeObject) {
    if (storeObject.isNewInstance()) {
      if (getQuery() != null) {
        return Future.failedFuture(new IllegalStateException("Can not update with a query and objects without id"));
      } else
        return Future.succeededFuture(
            new StoreObjectHolder(storeObject, BulkOperation.createInsert(storeObject.getContainer())));
    } else {
      IMapper<T> mapper = getMapper();
      Object currentId = storeObject.get(mapper.getIdInfo().getField());
      if (getQuery() != null) {
        IQuery<T> q = getDataStore().createQuery(getMapperClass());
        q.setSearchCondition(ISearchCondition.and(ISearchCondition.in(mapper.getIdInfo().getIndexedField(), currentId),
            getQuery().getSearchCondition()));
        Future<IQueryExpression> fQueryExp = Future.future();
        q.buildQueryExpression(null, fQueryExp);
        return fQueryExp.compose(queryExpression -> {
          JsonObject filter = ((MongoQueryExpression) queryExpression).getQueryDefinition();
          BulkOperation bulkOperation;
          if (partialUpdate)
            bulkOperation = createPartialUpdate(filter, storeObject.getContainer(), false);
          else
            bulkOperation = BulkOperation.createReplace(filter, storeObject.getContainer(), false);
          return Future.succeededFuture(new StoreObjectHolder(storeObject, bulkOperation));
        });
      } else {
        JsonObject filter = new JsonObject().put(MongoColumnInfo.ID_FIELD_NAME, currentId);
        BulkOperation bulkOperation;
        if (partialUpdate)
          bulkOperation = createPartialUpdate(filter, storeObject.getContainer(), true);
        else
          bulkOperation = BulkOperation.createReplace(filter, storeObject.getContainer(), true);
        return Future.succeededFuture(new StoreObjectHolder(storeObject, bulkOperation));
      }
    }
  }

  private BulkOperation createPartialUpdate(final JsonObject filter, final JsonObject object, final boolean upsert) {
    JsonObject document = new JsonObject().put("$set", object);
    if (setOnInsertFields != null) {
      document.put("$setOnInsert", setOnInsertFields);
    }
    return BulkOperation.createUpdate(filter, document, upsert, false);
  }

  private class StoreObjectHolder {
    private final MongoStoreObject<T> storeObject;
    private final BulkOperation bulkOperation;

    protected StoreObjectHolder(final MongoStoreObject<T> storeObject, final BulkOperation bulkOperation) {
      this.storeObject = storeObject;
      this.bulkOperation = bulkOperation;
    }

  }

  private Future<MongoStoreObject<T>> createStoreObject(final T entity) {
    Future<MongoStoreObject<T>> f = Future.future();
    ((MongoStoreObjectFactory) getDataStore().getStoreObjectFactory()).createStoreObject(getMapper(), entity, view,
        res -> f.handle(res.map(storeObject -> (MongoStoreObject<T>) storeObject)));
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

  private void finishQueryUpdate(final Object id, final T entity, final MongoStoreObject<T> storeObject,
      final MongoClientBulkWriteResult updateResult, final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    if (updateResult.getMatchedCount() != 0 && updateResult.getMatchedCount() == updateResult.getModifiedCount()) {
      finishUpdate(id, entity, storeObject, resultHandler);
    } else if (updateResult.getMatchedCount() == 0 && updateResult.getModifiedCount() == 0) {
      resultHandler.handle(Future.succeededFuture(new WriteEntry(storeObject, id, WriteAction.NOT_MATCHED)));
    } else {
      resultHandler.handle(Future.failedFuture(new WriteException("Matched " + updateResult.getMatchedCount()
          + "documents but modified: " + updateResult.getModifiedCount() + "documents")));
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

  public void setView(final Class<?> view) {
    this.view = view;
  }

  public void setSetOnInsertFields(final JsonObject setOnInsertFields) {
    this.setOnInsertFields = setOnInsertFields;
  }

}
