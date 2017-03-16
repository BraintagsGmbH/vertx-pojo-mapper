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

import de.braintags.vertx.jomnigate.dataaccess.impl.AbstractWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteEntry;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.dataaccess.write.WriteAction;
import de.braintags.vertx.jomnigate.dataaccess.write.impl.WriteEntry;
import de.braintags.vertx.jomnigate.exception.DuplicateKeyException;
import de.braintags.vertx.jomnigate.exception.WriteException;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mongo.MongoDataStore;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
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
    if (getObjectsToSave().isEmpty()) {
      resultHandler.handle(Future.succeededFuture(new MongoWriteResult()));
    } else {
      CompositeFuture cf = saveRecords();
      cf.setHandler(cfr -> {
        if (cfr.failed()) {
          resultHandler.handle(Future.failedFuture(cfr.cause()));
        } else {
          resultHandler.handle(Future.succeededFuture(new MongoWriteResult(cf.list())));
        }
      });
    }
  }

  @SuppressWarnings("rawtypes")
  private CompositeFuture saveRecords() {
    List<Future> fl = new ArrayList<>(getObjectsToSave().size());
    for (T entity : getObjectsToSave()) {
      fl.add(save(entity));
    }
    return CompositeFuture.all(fl);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Future<IWriteEntry> save(T entity) {
    Future<IWriteEntry> f = Future.future();
    getDataStore().getMapperFactory().getStoreObjectFactory().createStoreObject(getMapper(), entity, result -> {
      if (result.failed()) {
        WriteException we = new WriteException(result.cause());
        LOG.info("failed", we);
        f.fail(we);
      } else {
        doSave(entity, (MongoStoreObject) result.result(), sResult -> {
          if (sResult.failed()) {
            LOG.info("failed", sResult.cause());
            f.fail(sResult.cause());
          } else {
            f.complete(sResult.result());
          }
        });
      }
    });
    return f;
  }

  /**
   * execute the action to store ONE instance in mongo
   * 
   * @param storeObject
   * @param resultHandler
   */
  private void doSave(T entity, MongoStoreObject<T> storeObject, Handler<AsyncResult<IWriteEntry>> resultHandler) {
    LOG.debug("now saving: " + storeObject.toString());
    if (storeObject.isNewInstance()) {
      doInsert(entity, storeObject, resultHandler);
    } else {
      doUpdate(entity, storeObject, resultHandler);
    }

  }

  private void doInsert(T entity, MongoStoreObject<T> storeObject, Handler<AsyncResult<IWriteEntry>> resultHandler) {
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
  private void handleInsertError(Throwable t, T entity, MongoStoreObject<T> storeObject,
      Handler<AsyncResult<IWriteEntry>> resultHandler) {
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

  private void doUpdate(T entity, MongoStoreObject<T> storeObject, Handler<AsyncResult<IWriteEntry>> resultHandler) {
    MongoClient mongoClient = (MongoClient) ((MongoDataStore) getDataStore()).getClient();
    IMapper<T> mapper = getMapper();
    String collection = mapper.getTableInfo().getName();
    final Object currentId = storeObject.get(mapper.getIdField().getField());
    String idFieldName = mapper.getIdField().getField().getColumnInfo().getName();
    JsonObject query = new JsonObject();
    query.put(idFieldName, currentId);

    mongoClient.save(collection, storeObject.getContainer(), result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(new WriteException(result.cause())));
      } else {
        LOG.debug("updated");
        finishUpdate(currentId, entity, storeObject, resultHandler);
      }
    });
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void finishInsert(Object id, T entity, MongoStoreObject storeObject,
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

  @SuppressWarnings("rawtypes")
  private void finishUpdate(Object id, T entity, MongoStoreObject storeObject,
      Handler<AsyncResult<IWriteEntry>> resultHandler) {

    executePostSave(entity, lcr -> {
      if (lcr.failed()) {
        resultHandler.handle(Future.failedFuture(lcr.cause()));
      } else {
        resultHandler.handle(Future.succeededFuture(new WriteEntry(storeObject, id, WriteAction.UPDATE)));
      }
    });
  }

}
