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
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.Iterator;
import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.Query;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;
import de.braintags.io.vertx.pojomapper.mongo.mapper.MongoMapper;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class MongoQuery<T> extends Query<T> {

  /**
   * @param mapperClass
   * @param datastore
   */
  public MongoQuery(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public void execute(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    try {
      JsonObject query = createQueryDefinition();
      doFind(query, resultHandler);
    } catch (Throwable e) {
      Future<IQueryResult<T>> future = Future.failedFuture(e);
      resultHandler.handle(future);
    }
  }

  @Override
  public void executeCount(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    throw new UnsupportedOperationException();
  }

  private void doFind(JsonObject query, Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    MongoClient mongoClient = ((MongoDataStore) getDataStore()).getMongoClient();
    String column = getMapper().getDataStoreName();
    mongoClient.find(column, query, qResult -> {
      if (qResult.failed()) {
        Future<IQueryResult<T>> future = Future.failedFuture(qResult.cause());
        resultHandler.handle(future);
      } else {
        IQueryResult<T> qR = createQueryResult(qResult.result());
        Future<IQueryResult<T>> future = Future.succeededFuture(qR);
        resultHandler.handle(future);
      }
    });

  }

  private IQueryResult<T> createQueryResult(List<JsonObject> findList) {
    return new MongoQueryResult<T>(findList, (MongoDataStore) getDataStore(), (MongoMapper) getMapper());
  }

  private JsonObject createQueryDefinition() {
    JsonObject qDef = new JsonObject();
    Iterator<?> arguments = getFilters().iterator();
    while (arguments.hasNext()) {
      Object container = arguments.next();
      // TODO replace this with a Factory?
      if (container instanceof IFieldParameter<?>) {
        applyFieldParameter(qDef, (IFieldParameter<?>) container);
      } else if (container instanceof ILogicContainer<?>) {
        applyLogicParameter(qDef, (ILogicContainer<?>) container);
      } else
        throw new UnsupportedOperationException("unsupported type of query argument: " + container.getClass().getName());
    }
    return qDef;
  }

  private void applyFieldParameter(JsonObject qDef, IFieldParameter<?> fieldParameter) {
    IField field = fieldParameter.getField();
    String mongoOperator = QueryOperatorTranslator.translate(fieldParameter.getOperator());
    Object value = fieldParameter.getValue();
    Object storeObject = field.getTypeHandler().intoStore(value);
    qDef.put(field.getMappedFieldName(), new JsonObject().put(mongoOperator, storeObject));
  }

  private void applyLogicParameter(JsonObject qDef, ILogicContainer<?> logicContainer) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#parent()
   */
  @Override
  public Object parent() {
    return null;
  }

}
