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

import java.lang.reflect.Array;

import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
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

public class ArrayTypeHandler extends AbstractTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public ArrayTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler#matches(de.braintags.io.vertx.pojomapper.mapping
   * .IField)
   */
  @Override
  public short matches(IField field) {
    if (field.isArray() && (!field.hasAnnotation(Referenced.class) && (!field.hasAnnotation(Embedded.class))))
      return MATCH_MAJOR;

    return MATCH_NONE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    JsonArray jsonArray = (JsonArray) source;
    if (jsonArray == null || jsonArray.isEmpty())
      handler.handle(Future.succeededFuture());
    ErrorObject<ITypeHandlerResult> errorObject = new ErrorObject<ITypeHandlerResult>(handler);
    CounterObject co = new CounterObject(jsonArray.size());
    final Object resultArray = Array.newInstance(field.getSubClass(), jsonArray.size());
    int counter = 0;
    for (Object jo : jsonArray) {
      CurrentCounter cc = new CurrentCounter(counter++, jo);
      ITypeHandler subTypehandler = field.getSubTypeHandler();
      subTypehandler.fromStore(cc.value, field, field.getSubClass(), result -> {
        if (result.failed()) {
          errorObject.setThrowable(result.cause());
        } else {
          Object javaValue = result.result().getResult();
          if (javaValue != null)
            Array.set(resultArray, cc.i, javaValue);
          if (co.reduce()) {
            success(resultArray, handler);
          }
        }
      });
    }
    if (errorObject.isError())
      return;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void intoStore(Object javaValues, IField field, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    int length = javaValues == null ? 0 : Array.getLength(javaValues);
    if (length == 0)
      handler.handle(Future.succeededFuture());
    ErrorObject<ITypeHandlerResult> errorObject = new ErrorObject<ITypeHandlerResult>(handler);
    CounterObject co = new CounterObject(length);
    Object[] resultArray = new Object[length];
    for (int i = 0; i < length; i++) {
      // trying to write the array in the order like it is
      CurrentCounter cc = new CurrentCounter(i, Array.get(javaValues, i));
      ITypeHandler subTypehandler = field.getSubTypeHandler();
      subTypehandler.intoStore(cc.value, field, subResult -> {
        if (subResult.failed()) {
          errorObject.setThrowable(subResult.cause());
        } else {
          resultArray[cc.i] = subResult.result().getResult();
          if (co.reduce()) {
            JsonArray arr = new JsonArray();
            for (int k = 0; k < resultArray.length; k++) {
              arr.add(resultArray[k]);
            }
            success(arr, handler);
          }
        }
      });

      if (errorObject.isError())
        return;
    }
  }

  class CurrentCounter {
    int i;
    Object value;

    CurrentCounter(int i, Object value) {
      this.i = i;
      this.value = value;
    }
  }
}
