/*
 *
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

package de.braintags.io.vertx.pojomapper.json.typehandler.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class MapTypeHandler extends AbstractTypeHandler {

  /**
   * @param classesToDeal
   */
  public MapTypeHandler() {
    super(Map.class);
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
    ErrorObject<ITypeHandlerResult> errorObject = new ErrorObject<ITypeHandlerResult>();
    CounterObject co = new CounterObject(jsonArray.size());
    final MapEntry[] resultArray = new MapEntry[jsonArray.size()];
    int counter = 0;
    for (Object jo : jsonArray) {
      CurrentCounter cc = new CurrentCounter(counter++, jo);
      Object keyIn = ((JsonArray) cc.value).getValue(0);
      ITypeHandler th = field.getMapper().getMapperFactory().getDataStore().getTypeHandlerFactory()
          .getTypeHandler(field.getMapKeyClass());
      th.fromStore(keyIn, field, field.getMapKeyClass(), keyResult -> {
        if (keyResult.failed()) {
          errorObject.setThrowable(keyResult.cause());
        } else {
          Object valueIn = ((JsonArray) cc.value).getValue(1);
          convertValueFromStore(valueIn, field, valueResult -> {
            if (valueResult.failed()) {
              errorObject.setThrowable(valueResult.cause());
            } else {
              Object javaValue = valueResult.result();
              if (javaValue != null) {
                resultArray[cc.i] = new MapEntry(keyResult.result().getResult(), valueResult.result());
              }

              if (co.reduce()) {
                Map map = field.getMapper().getObjectFactory().createMap(field);
                for (int i = 0; i < resultArray.length; i++) {
                  map.put(resultArray[i].key, resultArray[i].value);
                }
                success(map, resultHandler);
              }
            }
          });
        }
      });

      if (errorObject.handleError(resultHandler))
        return;
    }
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
    ErrorObject<ITypeHandlerResult> errorObject = new ErrorObject<ITypeHandlerResult>();
    CounterObject co = new CounterObject(size);
    JsonArray[] resultArray = new JsonArray[size];
    Iterator<?> it = map.entrySet().iterator();
    int counter = 0;
    while (it.hasNext()) {
      // trying to write the array in the order like it is
      Entry entry = (Entry) it.next();
      CurrentCounter cc = new CurrentCounter(counter++, entry);
      ITypeHandler keyTh = getKeyTypeHandler(((Entry) cc.value).getKey(), field);

      keyTh.intoStore(
          ((Entry) cc.value).getKey(),
          field,
          keyResult -> {
            if (keyResult.failed()) {
              errorObject.setThrowable(keyResult.cause());
            } else {
              ITypeHandler valueTh = getValueTypeHandler(((Entry) cc.value).getValue(), field);
              valueTh.intoStore(
                  ((Entry) cc.value).getValue(),
                  field,
                  valueResult -> {
                    if (valueResult.failed()) {
                      errorObject.setThrowable(valueResult.cause());
                    } else {
                      resultArray[cc.i] = new JsonArray().add(keyResult.result().getResult()).add(
                          valueResult.result().getResult());
                      if (co.reduce()) {
                        JsonArray arr = new JsonArray();
                        for (int k = 0; k < resultArray.length; k++) {
                          arr.add(resultArray[k]);
                        }
                        success(arr, resultHandler);
                      }
                    }
                  });
            }
          });
      if (errorObject.handleError(resultHandler))
        return;
    }
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
