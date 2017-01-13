/*-
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
/*
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

package de.braintags.io.vertx.pojomapper.dataaccess.impl;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterSave;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Abstract implementation of {@link IWrite}
 * 
 * @author Michael Remme
 * @param <T>
 *          the underlaying mapper to be used
 */

public abstract class AbstractWrite<T> extends AbstractDataAccessObject<T> implements IWrite<T> {
  private List<T> objectsToSave = new ArrayList<>();

  /**
   * @param mapperClass
   * @param datastore
   */
  public AbstractWrite(final Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public final void save(Handler<AsyncResult<IWriteResult>> resultHandler) {
    sync(syncResult -> {
      if (syncResult.failed()) {
        resultHandler.handle(Future.failedFuture(syncResult.cause()));
      } else {
        try {
          internalSave(resultHandler);
        } catch (Exception e) {
          resultHandler.handle(Future.failedFuture(e));
        }
      }
    });
  }

  /**
   * This method is called after the sync call to execute the write action
   * 
   * @param resultHandler
   */
  protected abstract void internalSave(Handler<AsyncResult<IWriteResult>> resultHandler);

  /**
   * Get the objects that shall be saved
   * 
   * @return the objectsToSave
   */
  protected final List<T> getObjectsToSave() {
    return objectsToSave;
  }

  @Override
  public final void add(T mapper) {
    objectsToSave.add(mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite#add(java.util.List)
   */
  @Override
  public void addAll(List<T> mapperList) {
    for (T mapper : mapperList) {
      add(mapper);
    }
  }

  /**
   * execute the methods marked with {@link AfterSave}
   * 
   * @param entity
   *          the entity to be handled
   */
  protected void executePostSave(T entity, Handler<AsyncResult<Void>> resultHandler) {
    getMapper().executeLifecycle(AfterSave.class, entity, resultHandler);
  }

  /**
   * After inserting an instance, the id is placed into the entity and into the IStoreObject.
   * 
   * @param id
   *          the id to be stored
   * @param storeObject
   *          the instance of {@link IStoreObject}
   * @param resultHandler
   *          the handler to be informed
   */
  protected void setIdValue(Object id, IStoreObject<T, ? > storeObject, Handler<AsyncResult<Void>> resultHandler) {
    try {
      IField idField = getMapper().getIdField();
      storeObject.put(idField, id);
      idField.getPropertyMapper().fromStoreObject(storeObject.getEntity(), storeObject, idField, resultHandler);
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite#size()
   */
  @Override
  public int size() {
    return objectsToSave.size();
  }

}
