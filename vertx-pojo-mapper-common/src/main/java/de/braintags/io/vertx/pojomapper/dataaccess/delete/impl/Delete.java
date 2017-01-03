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
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.FieldCondition;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.exception.ParameterRequiredException;
import io.vertx.core.AsyncResult;
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
    if (recordList.isEmpty()) {
      resultHandler.handle(Future.succeededFuture());
      return;
    }
    CounterObject<IDeleteResult> co = new CounterObject<>(recordList.size(), resultHandler);
    for (T record : getRecordList()) {
      getMapper().executeLifecycle(BeforeDelete.class, record, lcr -> {
        if (lcr.failed()) {
          co.setThrowable(lcr.cause());
        } else {
          if (co.reduce()) {
            IField idField = getMapper().getIdField();
            getRecordIds(idField, res -> {
              if (res.failed()) {
                resultHandler.handle(Future.failedFuture(res.cause()));
              } else {
                deleteRecordsById(idField, res.result(), resultHandler);
              }
            });
          }
        }
      });
      if (co.isError()) {
        break;
      }
    }
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
    CounterObject<List<Object>> co = new CounterObject<>(getRecordList().size(), resultHandler);
    List<Object> values = new ArrayList<>();
    for (T record : getRecordList()) {
      idField.getPropertyMapper().readForStore(record, idField, vr -> {
        if (vr.failed()) {
          co.setThrowable(vr.cause());
          return;
        }
        values.add(vr.result());
        if (co.reduce()) {
          resultHandler.handle(Future.succeededFuture(values));
          return;
        }
      });
      if (co.isError())
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
    IQuery<T> q = getDataStore().createQuery(getMapperClass());
    q.setRootQueryPart(new FieldCondition(idField.getName(), QueryOperator.IN, objectIds));
    deleteQuery(q, dr -> {
      if (dr.failed()) {
        resultHandler.handle(dr);
      } else {
        CounterObject<IDeleteResult> co = new CounterObject<>(recordList.size(), resultHandler);
        for (T record : getRecordList()) {
          getMapper().executeLifecycle(AfterDelete.class, record, lcr -> {
            if (lcr.failed()) {
              co.setThrowable(lcr.cause());
            } else {
              if (co.reduce()) {
                resultHandler.handle(dr);
              }
            }
          });
          if (co.isError()) {
            break;
          }
        }
      }
    });
  }

}
