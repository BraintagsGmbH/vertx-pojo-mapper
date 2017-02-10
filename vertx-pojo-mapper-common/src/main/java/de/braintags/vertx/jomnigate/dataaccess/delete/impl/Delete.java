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
import java.util.List;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.lifecycle.AfterDelete;
import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeDelete;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDeleteResult;
import de.braintags.vertx.jomnigate.dataaccess.impl.AbstractDataAccessObject;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.mapping.IField;
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
  private List<T> recordList = new ArrayList<>();

  /**
   * @param mapperClass
   * @param datastore
   */
  public Delete(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.delete.IDelete#delete(io.vertx.core.Handler)
   */
  @Override
  public final void delete(Handler<AsyncResult<IDeleteResult>> resultHandler) {
    if (getQuery() != null) {
      deleteQuery(query, resultHandler);
    } else if (!getRecordList().isEmpty()) {
      deleteRecords(resultHandler);
    } else
      throw new ParameterRequiredException("Nor query nor records defined to be deleted");
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
  protected final void deleteRecords(Handler<AsyncResult<IDeleteResult>> resultHandler) {
    if (recordList.isEmpty()) {
      resultHandler.handle(Future.succeededFuture());
      return;
    }

    CompositeFuture cf = CompositeFuture.all(executeLifeCycle(BeforeDelete.class));
    cf.setHandler(res -> {
      if (res.failed()) {
        resultHandler.handle(Future.failedFuture(res.cause()));
      } else {
        doDeleteRecords(resultHandler);
      }
    });
  }

  private void doDeleteRecords(Handler<AsyncResult<IDeleteResult>> resultHandler) {
    IField idField = getMapper().getIdField();
    CompositeFuture cf = CompositeFuture.all(getRecordIds(idField));
    cf.setHandler(res -> {
      if (res.failed()) {
        resultHandler.handle(Future.failedFuture(res.cause()));
      } else {
        deleteRecordsById(idField, cf.list(), resultHandler);
      }
    });
  }

  @SuppressWarnings("rawtypes")
  private List<Future> executeLifeCycle(Class lifecycleClass) {
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
  public void setQuery(IQuery<T> query) {
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
  public void add(T record) {
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
  public void add(T... records) {
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
   * @return the recordList
   */
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
  private List<Future> getRecordIds(IField idField) {
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
  protected void deleteRecordsById(IField idField, List<Object> objectIds,
      Handler<AsyncResult<IDeleteResult>> resultHandler) {
    IQuery<T> q = getDataStore().createQuery(getMapperClass());
    q.setSearchCondition(ISearchCondition.in(idField.getName(), objectIds));
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
