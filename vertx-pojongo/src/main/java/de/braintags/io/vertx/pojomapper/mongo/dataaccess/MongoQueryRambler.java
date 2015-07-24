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
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.ArrayDeque;
import java.util.Iterator;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler;
import de.braintags.io.vertx.pojomapper.exception.QueryParameterException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.util.ErrorObject;

/**
 * Implementation fills the contents into a {@link JsonObject} which then can be used as source for
 * {@link MongoClient#find(String, JsonObject, io.vertx.core.Handler)}
 * 
 * @author Michael Remme
 * 
 */

public class MongoQueryRambler implements IQueryRambler {
  private JsonObject qDef = new JsonObject();
  private Object currentObject = qDef;
  private ArrayDeque<Object> deque = new ArrayDeque<>();

  /**
   * 
   */
  public MongoQueryRambler() {
  }

  /**
   * @return the jsonObject
   */
  public JsonObject getJsonObject() {
    return qDef;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#start(de.braintags.io.vertx.pojomapper.dataaccess
   * .query.IQuery)
   */
  @Override
  public void start(IQuery<?> query) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#stop(de.braintags.io.vertx.pojomapper.dataaccess
   * .query.IQuery)
   */
  @Override
  public void stop(IQuery<?> query) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#start(de.braintags.io.vertx.pojomapper.dataaccess
   * .query.ILogicContainer)
   */
  @Override
  public void start(ILogicContainer<?> container) {
    String logic = QueryLogicTranslator.translate(container.getLogic());
    JsonArray array = new JsonArray();
    add(logic, array);
    deque.addLast(currentObject);
    currentObject = array;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#stop(de.braintags.io.vertx.pojomapper.dataaccess
   * .query.ILogicContainer)
   */
  @Override
  public void stop(ILogicContainer<?> container) {
    currentObject = deque.pollLast();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#start(de.braintags.io.vertx.pojomapper.dataaccess
   * .query.IFieldParameter)
   */
  @Override
  public void start(IFieldParameter<?> fieldParameter, Handler<AsyncResult<Void>> resultHandler) {

    switch (fieldParameter.getOperator()) {
    case IN:
    case NOT_IN:
      handleMultipleValues(fieldParameter, resultHandler);
      break;

    default:
      handleSingleValue(fieldParameter, resultHandler);
    }
  }

  /**
   * Create the argument for query parts, which define one single argument
   * 
   * @param fieldParameter
   * @param resultHandler
   */
  private void handleMultipleValues(IFieldParameter<?> fieldParameter, Handler<AsyncResult<Void>> resultHandler) {
    IField field = fieldParameter.getField();
    String mongoOperator = QueryOperatorTranslator.translate(fieldParameter.getOperator());
    Object valueIterable = fieldParameter.getValue();
    if (!(valueIterable instanceof Iterable)) {
      resultHandler.handle(Future.failedFuture(new QueryParameterException(
          "multivalued argument but not an instance of Iterable")));
      return;
    }
    Iterator<?> values = ((Iterable<?>) valueIterable).iterator();
    ErrorObject<Void> errorObject = new ErrorObject<Void>();
    JsonArray resultArray = new JsonArray();

    while (values.hasNext()) {
      Object value = values.next();
      field.getTypeHandler().intoStore(value, field, result -> {
        if (result.failed()) {
          errorObject.setThrowable(result.cause());
        } else {
          resultArray.add(result.result().getResult());
        }
      });
    }
    if (errorObject.handleResult(resultHandler))
      return;
    JsonObject arg = new JsonObject().put(mongoOperator, resultArray);
    add(field.getMappedFieldName(), arg);
    resultHandler.handle(Future.succeededFuture());

  }

  /**
   * Create the argument for query parts, which define one single argument
   * 
   * @param fieldParameter
   * @param resultHandler
   */
  private void handleSingleValue(IFieldParameter<?> fieldParameter, Handler<AsyncResult<Void>> resultHandler) {
    IField field = fieldParameter.getField();
    String mongoOperator = QueryOperatorTranslator.translate(fieldParameter.getOperator());
    Object value = fieldParameter.getValue();

    field.getTypeHandler().intoStore(value, field, result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
      } else {
        Object storeObject = result.result().getResult();
        JsonObject arg = new JsonObject().put(mongoOperator, storeObject);
        add(field.getMappedFieldName(), arg);
        resultHandler.handle(Future.succeededFuture());
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryRambler#stop(de.braintags.io.vertx.pojomapper.dataaccess
   * .query.IFieldParameter)
   */
  @Override
  public void stop(IFieldParameter<?> fieldParameter) {
  }

  private void add(String key, Object objectToAdd) {
    if (currentObject instanceof JsonObject) {
      ((JsonObject) currentObject).put(key, objectToAdd);
    } else if (currentObject instanceof JsonArray) {
      JsonObject ob = new JsonObject().put(key, objectToAdd);
      ((JsonArray) currentObject).add(ob);
    } else
      throw new UnsupportedOperationException("no definition to add for " + currentObject.getClass().getName());
  }

}
