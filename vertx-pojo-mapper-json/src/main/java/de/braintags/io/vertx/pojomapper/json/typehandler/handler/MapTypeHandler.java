/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.json.typehandler.handler;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class MapTypeHandler extends AbstractTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public MapTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Map.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    JsonArray jsonArray = (JsonArray) source;
    if (jsonArray == null || jsonArray.isEmpty())
      resultHandler.handle(Future.succeededFuture());

    ErrorObject<ITypeHandlerResult> errorObject = new ErrorObject<ITypeHandlerResult>(resultHandler);
    CounterObject co = new CounterObject(jsonArray.size());
    final MapEntry[] resultArray = new MapEntry[jsonArray.size()];
    int counter = 0;
    for (Object jo : jsonArray) {
      CurrentCounter cc = new CurrentCounter(counter++, jo);
      handleOneEntryFromStore(field, cc, resultArray, result -> {
        if (result.failed()) {
          errorObject.setThrowable(result.cause());
          return;
        } else {
          checkSuccessFromStore(field, co, resultArray, resultHandler);
        }
      });
      if (errorObject.isError())
        return;
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void checkSuccessFromStore(IField field, CounterObject co, MapEntry[] resultArray,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (co.reduce()) {
      Map map = field.getMapper().getObjectFactory().createMap(field);
      for (int i = 0; i < resultArray.length; i++) {
        map.put(resultArray[i].key, resultArray[i].value);
      }
      success(map, resultHandler);
    }
  }

  private void handleOneEntryFromStore(IField field, CurrentCounter cc, MapEntry[] resultArray,
      Handler<AsyncResult<Void>> resultHandler) {
    Object keyIn = ((JsonArray) cc.value).getValue(0);
    ITypeHandler th = field.getMapper().getMapperFactory().getDataStore().getTypeHandlerFactory()
        .getTypeHandler(field.getMapKeyClass());
    th.fromStore(keyIn, field, field.getMapKeyClass(), keyResult -> {
      if (keyResult.failed()) {
        resultHandler.handle(Future.failedFuture(keyResult.cause()));
        return;
      } else {
        Object valueIn = ((JsonArray) cc.value).getValue(1);
        convertValueFromStore(valueIn, field, valueResult -> {
          if (valueResult.failed()) {
            resultHandler.handle(Future.failedFuture(valueResult.cause()));
            return;
          } else {
            Object javaValue = valueResult.result();
            if (javaValue != null) {
              resultArray[cc.i] = new MapEntry(keyResult.result().getResult(), valueResult.result());
            }
            resultHandler.handle(Future.succeededFuture());
          }
        });
      }
    });

  }

  private void convertValueFromStore(Object valueIn, IField field, Handler<AsyncResult<Object>> resultHandler) {
    if (field.getSubTypeHandler() == null) {
      resultHandler.handle(Future.succeededFuture(valueIn));
      return;
    }
    field.getSubTypeHandler().fromStore(valueIn, field, field.getSubClass(), valueResult -> {
      if (valueResult.failed()) {
        resultHandler.handle(Future.failedFuture(valueResult.cause()));
      } else {
        Object javaValue = valueResult.result().getResult();
        resultHandler.handle(Future.succeededFuture(javaValue));
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    Map<?, ?> map = (Map<?, ?>) source;
    int size = map == null ? 0 : map.size();
    if (size == 0)
      resultHandler.handle(Future.succeededFuture());
    ErrorObject<ITypeHandlerResult> errorObject = new ErrorObject<ITypeHandlerResult>(resultHandler);
    CounterObject co = new CounterObject(size);
    JsonArray[] resultArray = new JsonArray[size];
    Iterator<?> it = map.entrySet().iterator();
    int counter = 0;
    while (it.hasNext() && !errorObject.isError()) {
      // trying to write the array in the order like it is
      Entry entry = (Entry) it.next();
      CurrentCounter cc = new CurrentCounter(counter++, entry);

      valueIntoStore(field, cc, resultArray, result -> {
        if (result.failed()) {
          errorObject.setThrowable(result.cause());
          return;
        } else {
          checkSuccessIntoStore(co, resultArray, resultHandler);
        }
      });
    }
  }

  private void checkSuccessIntoStore(CounterObject co, JsonArray[] resultArray,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (co.reduce()) {
      JsonArray arr = new JsonArray();
      for (int k = 0; k < resultArray.length; k++) {
        arr.add(resultArray[k]);
      }
      success(arr, resultHandler);
    }
  }

  @SuppressWarnings("rawtypes")
  private void valueIntoStore(IField field, CurrentCounter cc, JsonArray[] resultArray,
      Handler<AsyncResult<Void>> resultHandler) {
    ITypeHandler keyTh = getKeyTypeHandler(((Entry) cc.value).getKey(), field);

    keyTh.intoStore(((Entry) cc.value).getKey(), field, keyResult -> {
      if (keyResult.failed()) {
        resultHandler.handle(Future.failedFuture(keyResult.cause()));
        return;
      } else {
        ITypeHandler valueTh = getValueTypeHandler(((Entry) cc.value).getValue(), field);
        valueTh.intoStore(((Entry) cc.value).getValue(), field, valueResult -> {
          if (valueResult.failed()) {
            resultHandler.handle(Future.failedFuture(valueResult.cause()));
            return;
          } else {
            resultArray[cc.i] = new JsonArray().add(keyResult.result().getResult())
                .add(valueResult.result().getResult());
            resultHandler.handle(Future.succeededFuture());
          }
        });
      }
    });
  }

  @SuppressWarnings("rawtypes")
  private ITypeHandler getValueTypeHandler(Object value, IField field) {
    Class valueClass = field.getSubClass();
    if (valueClass == null || valueClass == Object.class)
      valueClass = value.getClass();
    return field.getMapper().getMapperFactory().getDataStore().getTypeHandlerFactory().getTypeHandler(valueClass);
  }

  @SuppressWarnings("rawtypes")
  private ITypeHandler getKeyTypeHandler(Object value, IField field) {
    Class keyClass = field.getMapKeyClass();
    if (keyClass == null || keyClass == Object.class)
      keyClass = value.getClass();
    return field.getMapper().getMapperFactory().getDataStore().getTypeHandlerFactory().getTypeHandler(keyClass);
  }

  class CurrentCounter {
    int i;
    Object value;

    CurrentCounter(int i, Object value) {
      this.i = i;
      this.value = value;
    }
  }

  class MapEntry {
    Object key;
    Object value;

    MapEntry(Object key, Object value) {
      this.key = key;
      this.value = value;
    }
  }

}
