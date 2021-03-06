/*
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

package de.braintags.vertx.jomnigate.dataaccess.delete.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterDelete;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeDelete;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDeleteResult;
import de.braintags.vertx.jomnigate.dataaccess.impl.AbstractDataAccessObject;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.IdField;
import de.braintags.vertx.jomnigate.mapping.IIdInfo;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.observer.IObserverContext;
import de.braintags.vertx.util.exception.ParameterRequiredException;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Abstract implementation of {@link IDelete}
 * 
 * @author Michael Remme
 * @param <T>
 *          the underlaying mapper to be used
 */

public abstract class Delete<T> extends AbstractDataAccessObject<T> implements IDelete<T> {

  private static final String ERROR_MESSAGE = "You can only use ONE source for deletion, either an IQuery or a list of instances";
  private IQuery<T> query;
  private final List<T> recordList = new ArrayList<>();

  /**
   * @param mapperClass
   * @param datastore
   */
  public Delete(final Class<T> mapperClass, final IDataStore datastore) {
    super(mapperClass, datastore);
  }

  /**
   * Get the objects, which were defined to be deleted
   * 
   * @return
   */
  Iterator<T> getSelection() {
    return recordList.iterator();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.delete.IDelete#delete(io.vertx.core.Handler)
   */
  @Override
  public final void delete(final Handler<AsyncResult<IDeleteResult>> resultHandler) {
    if (getQuery() != null) {
      deleteQuery(query, resultHandler);
    } else if (!recordList.isEmpty()) {
      deleteRecords(resultHandler);
    } else
      throw new ParameterRequiredException("Nor query nor records defined to be deleted");
  }

  @Override
  public int size() {
    return recordList.size();
  }

  /**
   * This method deletes all records, which are fitting the query arguments
   * 
   * @param query
   *          the query to be handled
   * @param resultHandler
   *          the handler to be informed
   */
  protected abstract void deleteQuery(IQuery<T> query, Handler<AsyncResult<IDeleteResult>> resultHandler);

  /**
   * This method deletes records, which were added into the current instance
   * 
   * @param resultHandler
   *          the handler to be informed
   */
  protected final void deleteRecords(final Handler<AsyncResult<IDeleteResult>> resultHandler) {
    if (recordList.isEmpty()) {
      resultHandler.handle(Future.succeededFuture());
      return;
    }

    CompositeFuture cf = CompositeFuture.all(executeLifeCycle(BeforeDelete.class));
    cf.setHandler(res -> {
      if (res.failed()) {
        resultHandler.handle(Future.failedFuture(res.cause()));
      } else {
        try {
          Future<IDeleteResult> rf = Future.future();
          rf.setHandler(resultHandler);
          IObserverContext context = IObserverContext.createInstance();
          preDelete(context).compose(pre -> doDeleteRecords()).compose(dr -> postDelete(dr, context, rf), rf);
        } catch (Exception e) {
          resultHandler.handle(Future.failedFuture(e));
        }
      }
    });
  }

  private Future<IDeleteResult> doDeleteRecords() {
    Future<IDeleteResult> f = Future.future();
    IIdInfo idInfo = getMapper().getIdInfo();
    IdField idField = idInfo.getIndexedField();
    CompositeFuture cf = CompositeFuture.all(getRecordIds(idInfo.getField()));
    cf.setHandler(res -> {
      if (res.failed()) {
        f.fail(res.cause());
      } else {
        deleteRecordsById(idField, cf.list(), f);
      }
    });
    return f;
  }

  /**
   * Execution done before instances are deleted from the datastore
   * 
   * @return
   */
  protected Future<Void> preDelete(final IObserverContext context) {
    return getMapper().getObserverHandler().handleBeforeDelete(this, context);
  }

  /**
   * Execution done after entities were deleted from the datastore
   * 
   * @param wr
   * @param nextFuture
   */
  protected void postDelete(final IDeleteResult dr, final IObserverContext context,
      final Future<IDeleteResult> nextFuture) {
    Future<Void> f = getMapper().getObserverHandler().handleAfterDelete(this, dr, context);
    f.setHandler(res -> {
      if (res.failed()) {
        nextFuture.fail(res.cause());
      } else {
        nextFuture.complete(dr);
      }
    });
  }

  @SuppressWarnings("rawtypes")
  private List<Future> executeLifeCycle(final Class lifecycleClass) {
    List<Future> fl = new ArrayList<>();
    for (T record : getRecordList()) {
      Future<Void> f = Future.future();
      getMapper().executeLifecycle(lifecycleClass, record, f.completer());
      fl.add(f);
    }
    return fl;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.delete.IDelete#setQuery(de.braintags.vertx.jomnigate.dataaccess
   * .query.IQuery)
   */
  @Override
  public void setQuery(final IQuery<T> query) {
    if (!recordList.isEmpty())
      throw new UnsupportedOperationException(ERROR_MESSAGE);
    this.query = query;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.delete.IDelete#add(java.lang.Object)
   */
  @Override
  public void add(final T record) {
    if (query != null)
      throw new UnsupportedOperationException(ERROR_MESSAGE);
    recordList.add(record);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.delete.IDelete#add(java.lang.Object[])
   */
  @SuppressWarnings("unchecked")
  @Override
  public void add(final T... records) {
    if (query != null)
      throw new UnsupportedOperationException(ERROR_MESSAGE);
    recordList.addAll(Arrays.asList(records));
  }

  /**
   * @return the query
   */
  protected IQuery<T> getQuery() {
    return query;
  }

  /**
   * @deprecated use getSelection() instead
   */
  @Deprecated
  protected List<T> getRecordList() {
    return recordList;
  }

  /**
   * Generates a list of record ids from the records
   * 
   * @param idField
   * @param resultHandler
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private List<Future> getRecordIds(final IProperty idField) {
    List<Future> fList = new ArrayList<>();
    for (T record : getRecordList()) {
      Future f = Future.future();
      idField.getPropertyMapper().readForStore(record, idField, f.completer());
      fList.add(f);
    }
    return fList;
  }

  /**
   * Performs a deletion of instances by their ID
   * 
   * @param idField
   *          the idfield
   * @param objectIds
   *          list of recordIds
   * @param resultHandler
   *          the handler to be informed
   */
  protected void deleteRecordsById(final IdField idField, final List<Object> objectIds,
      final Handler<AsyncResult<IDeleteResult>> resultHandler) {
    IQuery<T> q = getDataStore().createQuery(getMapperClass());
    q.setSearchCondition(ISearchCondition.in(idField, objectIds));
    deleteQuery(q, dr -> {
      if (dr.failed()) {
        resultHandler.handle(dr);
      } else {
        CompositeFuture cf = CompositeFuture.all(executeLifeCycle(AfterDelete.class));
        cf.setHandler(res -> {
          if (res.failed()) {
            resultHandler.handle(Future.failedFuture(res.cause()));
          } else {
            resultHandler.handle(dr);
          }
        });
      }
    });
  }

}
