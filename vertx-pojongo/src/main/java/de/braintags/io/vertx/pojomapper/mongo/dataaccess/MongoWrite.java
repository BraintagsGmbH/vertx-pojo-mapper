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
import de.braintags.io.vertx.pojomapper.dataaccess.IWrite;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;

/**
 * @author Michael Remme
 * @param <T>
 */

public class MongoWrite<T> extends AbstractMongoAccessObject<T> implements IWrite<T> {
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
    for (T entity : objectsToSave) {
      save(entity, resultHandler);
    }
  }

  private void save(T entity, Handler<AsyncResult<IWriteResult>> resultHandler) {
    executePreSave(entity);
    MongoStoreObject storeObject = createStoreObject(entity);
    doSave(storeObject, resultHandler);
    executePostSave(entity);
  }

  private MongoStoreObject createStoreObject(T entity) {
    MongoStoreObject store = new MongoStoreObject();
    for (String fieldName : getMapper().getFieldNames()) {
      IField field = getMapper().getField(fieldName);
      field.getPropertyMapper().intoStoreObject(entity, store, field);
    }
    return store;
  }

  /**
   * execute the methods marked with {@link BeforeSave}
   * 
   * @param entity
   *          the entity to be handled
   */
  private void executePreSave(T entity) {
    getMapper().executeLifecycle(AfterSave.class, entity);
  }

  /**
   * execute the methods marked with {@link AfterSave}
   * 
   * @param entity
   *          the entity to be handled
   */
  private void executePostSave(T entity) {
    getMapper().executeLifecycle(BeforeSave.class, entity);
  }

  /**
   * execute the action to store the instance in mongo
   * 
   * @param storeObject
   * @param resultHandler
   */
  private void doSave(MongoStoreObject storeObject, Handler<AsyncResult<IWriteResult>> resultHandler) {
    MongoClient mongoClient = ((MongoDataStore) getDataStore()).getMongoClient();
    IMapper mapper = getMapper();
    String column = mapper.getDataStoreName();
    final String currentId = (String) storeObject.get(mapper.getIdField());

    mongoClient.save(column, storeObject.getContainer(), result -> {
      if (result.failed()) {
        Future<IWriteResult> future = Future.failedFuture(result.cause());
        resultHandler.handle(future);
        return;
      } else {
        String id = result.result();
        if (id == null)
          id = currentId;
        Future<IWriteResult> future = Future.succeededFuture(new MongoWriteResult(storeObject, id));
        resultHandler.handle(future);
      }
    });

  }

  class MongoWriteResult implements IWriteResult {
    private IStoreObject<?> sto;
    private String id;

    MongoWriteResult(IStoreObject<?> sto, String id) {
      this.sto = sto;
      this.id = id;
    }

    @Override
    public IStoreObject<?> getStoreObject() {
      return sto;
    }

    @Override
    public Object getId() {
      return id;
    }

  }
}
