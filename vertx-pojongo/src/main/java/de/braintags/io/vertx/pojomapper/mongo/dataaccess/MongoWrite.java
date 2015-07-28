/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.mongo.MongoClient;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterSave;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeSave;
import de.braintags.io.vertx.pojomapper.dataaccess.impl.AbstractDataAccessObject;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.dataaccess.write.impl.WriteResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;

/**
 * @author Michael Remme
 * @param <T>
 */

public class MongoWrite<T> extends AbstractDataAccessObject<T> implements IWrite<T> {
  private List<T> objectsToSave = new ArrayList<T>();

  /**
   * 
   */
  public MongoWrite(final Class<T> mapperClass, MongoDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public void add(T mapper) {
    objectsToSave.add(mapper);
  }

  @Override
  public void save(Handler<AsyncResult<IWriteResult>> resultHandler) {
    WriteResult rr = new WriteResult();
    if (objectsToSave.isEmpty()) {
      resultHandler.handle(Future.succeededFuture(rr));
      return;
    }
    ErrorObject<IWriteResult> ro = new ErrorObject<IWriteResult>();
    CounterObject counter = new CounterObject(objectsToSave.size());
    for (T entity : objectsToSave) {
      save(entity, rr, result -> {
        if (result.failed()) {
          ro.setThrowable(result.cause());
        } else {
          if (counter.reduce())
            resultHandler.handle(Future.succeededFuture(rr));
        }
      });
      if (ro.handleError(resultHandler))
        return;
    }
  }

  private void save(T entity, IWriteResult writeResult, Handler<AsyncResult<Void>> resultHandler) {
    executePreSave(entity);
    MongoStoreObject storeObject = new MongoStoreObject(getMapper(), entity);
    storeObject.initFromEntity(initResult -> {
      if (initResult.failed()) {
        resultHandler.handle(Future.failedFuture(initResult.cause()));
      } else {
        doSave(entity, storeObject, writeResult, sResult -> {
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
   * execute the methods marked with {@link BeforeSave}
   * 
   * @param entity
   *          the entity to be handled
   */
  private void executePreSave(T entity) {
    getMapper().executeLifecycle(BeforeSave.class, entity);
  }

  /**
   * execute the methods marked with {@link AfterSave}
   * 
   * @param entity
   *          the entity to be handled
   */
  private void executePostSave(T entity) {
    getMapper().executeLifecycle(AfterSave.class, entity);
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
    String column = mapper.getDataStoreName();
    final String currentId = (String) storeObject.get(mapper.getIdField());

    mongoClient.save(column, storeObject.getContainer(), result -> {
      if (result.failed()) {
        Future<Void> future = Future.failedFuture(result.cause());
        resultHandler.handle(future);
        return;
      } else {
        WriteAction action = WriteAction.UNKNOWN;
        String id = result.result();
        if (id == null) {
          id = currentId;
          action = WriteAction.UPDATE;
        } else
          action = WriteAction.INSERT;
        executePostSave(entity);
        writeResult.addEntry(storeObject, id, action);
        Future<Void> future = Future.succeededFuture();
        resultHandler.handle(future);
      }
    });

  }

}
