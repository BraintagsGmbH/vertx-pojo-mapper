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

package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.mongo.MongoClient;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterDelete;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.impl.Delete;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.impl.DeleteResult;
import de.braintags.io.vertx.pojomapper.exception.ParameterRequiredException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class MongoDelete<T> extends Delete<T> {

  /**
   * @param mapperClass
   * @param datastore
   */
  public MongoDelete(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete#delete(io.vertx.core.Handler)
   */
  @Override
  public void delete(Handler<AsyncResult<IDeleteResult>> resultHandler) {
    if (getQuery() != null) {
      deleteQuery((MongoQuery<T>) getQuery(), resultHandler);
    } else if (!getRecordList().isEmpty()) {
      deleteRecords(resultHandler);
    } else
      throw new ParameterRequiredException("Nor query nor records defined to be deleted");
  }

  private void deleteRecords(Handler<AsyncResult<IDeleteResult>> resultHandler) {
    MongoQuery<T> query = (MongoQuery<T>) getDataStore().createQuery(getMapperClass());
    IField idField = getMapper().getIdField();
    List<Object> values = new ArrayList<Object>();
    for (T record : getRecordList()) {
      getMapper().executeLifecycle(BeforeDelete.class, record);
      values.add(idField.getPropertyAccessor().readData(record));
    }
    query.field(idField.getName()).in(values);
    deleteQuery(query, dr -> {
      for (T record : getRecordList()) {
        getMapper().executeLifecycle(AfterDelete.class, record);
      }
      resultHandler.handle(dr);
    });
  }

  private void deleteQuery(MongoQuery<T> query, Handler<AsyncResult<IDeleteResult>> resultHandler) {
    MongoClient mongoClient = ((MongoDataStore) getDataStore()).getMongoClient();
    String collection = getMapper().getDataStoreName();
    query.createQueryDefinition(qDefResult -> {
      if (qDefResult.failed()) {
        resultHandler.handle(Future.failedFuture(qDefResult.cause()));
      } else {
        mongoClient.remove(collection, qDefResult.result(), deleteHandler -> {
          if (deleteHandler.failed()) {
            resultHandler.handle(Future.failedFuture(deleteHandler.cause()));
          } else {
            DeleteResult deleteResult = new DeleteResult(getDataStore(), getMapper(), qDefResult.result());
            resultHandler.handle(Future.succeededFuture(deleteResult));
          }
        });
      }
    });
  }

}
