/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.dataaccess.delete.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterDelete;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.impl.AbstractDataAccessObject;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.exception.ParameterRequiredException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Abstract implementation of {@link IDelete}
 * 
 * @author Michael Remme
 * 
 */

public abstract class Delete<T> extends AbstractDataAccessObject<T>implements IDelete<T> {
  private static final String ERROR_MESSAGE = "You can only use ONE source for deletion, either an IQuery or a list of instances";
  private IQuery<T> query;
  private List<T> recordList = new ArrayList<T>();

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
   * @see de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete#delete(io.vertx.core.Handler)
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
    IField idField = getMapper().getIdField();
    try {
      for (T record : getRecordList()) {
        getMapper().executeLifecycle(BeforeDelete.class, record);
      }
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
      return;
    }
    getRecordIds(idField, res -> {
      if (res.failed()) {
        resultHandler.handle(Future.failedFuture(res.cause()));
        return;
      }
      deleteRecordsById(idField, res.result(), resultHandler);
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete#setQuery(de.braintags.io.vertx.pojomapper.dataaccess
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
   * @see de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete#add(java.lang.Object)
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
   * @see de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete#add(java.lang.Object[])
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
  protected void getRecordIds(IField idField, Handler<AsyncResult<List<Object>>> resultHandler) {
    CounterObject co = new CounterObject(getRecordList().size());
    ErrorObject<List<Object>> err = new ErrorObject<List<Object>>(resultHandler);
    List<Object> values = new ArrayList<Object>();
    for (T record : getRecordList()) {
      idField.getPropertyMapper().readForStore(record, idField, vr -> {
        if (vr.failed()) {
          err.setThrowable(vr.cause());
          return;
        }
        values.add(vr.result());
        if (co.reduce()) {
          resultHandler.handle(Future.succeededFuture(values));
          return;
        }
      });
      if (err.isError())
        return;
    }
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
    IQuery<T> query = getDataStore().createQuery(getMapperClass());
    query.field(idField.getName()).in(objectIds);
    deleteQuery(query, dr -> {
      if (dr.failed()) {
        resultHandler.handle(dr);
        return;
      }
      try {
        for (T record : getRecordList()) {
          getMapper().executeLifecycle(AfterDelete.class, record);
        }
      } catch (Exception e) {
        resultHandler.handle(Future.failedFuture(e));
        return;
      }
      resultHandler.handle(dr);
    });
  }

}
