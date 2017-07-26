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

package de.braintags.vertx.jomnigate.dataaccess.write.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterSave;
import de.braintags.vertx.jomnigate.dataaccess.impl.AbstractDataAccessObject;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.IPropertyAccessor;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
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

  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractWrite.class);

  private final List<T> objectsToSave = new ArrayList<>();
  private IQuery<T> query;

  /**
   * @param mapperClass
   * @param datastore
   */
  public AbstractWrite(final Class<T> mapperClass, final IDataStore<?, ?> datastore) {
    super(mapperClass, datastore);
  }

  /**
   * Get the objects, which were defined to be saved
   * 
   * @return
   */
  Iterator<T> getSelection() {
    return objectsToSave.iterator();
  }

  @Override
  public final void save(final Handler<AsyncResult<IWriteResult>> resultHandler) {
    sync(syncResult -> {
      if (syncResult.failed()) {
        resultHandler.handle(Future.failedFuture(syncResult.cause()));
      } else {
        try {
          Future<IWriteResult> rf = Future.future();
          rf.setHandler(resultHandler);
          IObserverContext context = IObserverContext.createInstance();
          internalSave(context).compose(wr -> postSave(wr, context, rf), rf);
        } catch (Exception e) {
          resultHandler.handle(Future.failedFuture(e));
        }
      }
    });
  }

  /**
   * Execution done after entities were stored into the datastore
   * 
   * @param wr
   * @param nextFuture
   */
  protected void postSave(final IWriteResult wr, final IObserverContext context,
      final Future<IWriteResult> nextFuture) {
    Future<Void> f = getMapper().getObserverHandler().handleAfterInsert(this, wr, context);
    f.setHandler(res -> {
      if (f.failed()) {
        nextFuture.fail(f.cause());
      } else {
        nextFuture.complete(wr);
      }
    });
  }

  /**
   * This method is called after the sync call to execute the write action
   * 
   * @param resultHandler
   */
  protected abstract Future<IWriteResult> internalSave(IObserverContext context);

  /**
   * Get the objects that shall be saved
   * 
   * @return the objectsToSave
   */
  protected final List<T> getObjectsToSave() {
    return objectsToSave;
  }

  @Override
  public final void add(final T mapper) {
    objectsToSave.add(mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.write.IWrite#add(java.util.List)
   */
  @Override
  public void addAll(final Collection<T> mapperList) {
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
  protected void executePostSave(final T entity, final Handler<AsyncResult<Void>> resultHandler) {
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
  protected void setIdValue(final Object id, final IStoreObject<T, ?> storeObject,
      final Handler<AsyncResult<Void>> resultHandler) {
    try {
      IProperty idField = getMapper().getIdInfo().getField();
      // storeObject.put(idField, id.toString());
      IPropertyAccessor acc = idField.getPropertyAccessor();
      acc.writeData(storeObject.getEntity(), id.toString());
      // idField.getPropertyMapper().fromStoreObject(storeObject.getEntity(), storeObject, idField, resultHandler);
      resultHandler.handle(Future.succeededFuture());
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.write.IWrite#size()
   */
  @Override
  public int size() {
    return objectsToSave.size();
  }

  /**
   * @return the query
   */
  protected IQuery<T> getQuery() {
    return query;
  }

  @Override
  public void setQuery(final IQuery<T> query) {
    this.query = query;
  }
}
