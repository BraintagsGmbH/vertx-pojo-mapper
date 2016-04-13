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

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.exception.InsertException;
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
   * @see de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler#matchesClass(java.lang.Class)
   */
  @Override
  protected short matchesClass(Class<?> cls) {
    return cls.isArray() ? MATCH_MAJOR : MATCH_NONE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler#matchesAnnotation(java.lang.annotation.Annotation)
   */
  @Override
  protected boolean matchesAnnotation(Annotation annotation) {
    return annotation == null;
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
    CounterObject<ITypeHandlerResult> co = new CounterObject<ITypeHandlerResult>(jsonArray.size(), handler);
    final Object resultArray = Array.newInstance(field.getSubClass(), jsonArray.size());
    int counter = 0;
    for (Object jo : jsonArray) {
      CurrentCounter cc = new CurrentCounter(counter++, jo);
      ITypeHandler subTypehandler = field.getSubTypeHandler();
      subTypehandler.fromStore(cc.value, field, field.getSubClass(), result -> {
        if (result.failed()) {
          co.setThrowable(result.cause());
          return;
        }
        Object javaValue = result.result().getResult();
        if (javaValue != null)
          Array.set(resultArray, cc.i, javaValue);
        if (co.reduce()) {
          success(resultArray, handler);
        }

      });
    }
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
    if (length == 0) {
      handler.handle(Future.succeededFuture());
    } else {
      ITypeHandler subTypehandler = field.getSubTypeHandler();
      CounterObject<ITypeHandlerResult> co = new CounterObject<>(length, handler);
      ResultArray resultArray = new ResultArray(length);
      for (int i = 0; i < length; i++) {
        // trying to write the array in the order like it is
        final CurrentCounter cc = new CurrentCounter(i, Array.get(javaValues, i));
        writeEntry(cc, co, resultArray, subTypehandler, field, handler);
        if (co.isError()) {
          return;
        }
      }
    }
  }

  private void writeEntry(final CurrentCounter cc, CounterObject<ITypeHandlerResult> co, ResultArray resultArray,
      ITypeHandler subTypehandler, IField field, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    subTypehandler.intoStore(cc.value, field, subResult -> {
      if (subResult.failed()) {
        co.setThrowable(subResult.cause());
      } else {
        resultArray.add(cc.i, subResult.result().getResult(), co);
        if (co.reduce()) {
          JsonArray arr = resultArray.toJsonArray(co);
          if (!co.isError()) {
            success(arr, handler);
          }
        }
      }
    });
  }

  class ResultArray {
    final List contentList;
    final Object[] content;

    ResultArray(int length) {
      content = new Object[length];
      contentList = new ArrayList<>();
    }

    void add(int position, Object instance, ErrorObject<ITypeHandlerResult> errorObject) {
      if (content[position] != null) {
        errorObject.setThrowable(new InsertException(String.format(
            "Trying to write an entry, which was filled already. Old: %s | new: %s", content[position], instance)));
      } else {
        content[position] = instance;
        contentList.add(instance);
      }

    }

    JsonArray toJsonArray(ErrorObject<ITypeHandlerResult> errorObject) {
      JsonArray arr = new JsonArray();
      for (int k = 0; k < content.length; k++) {
        arr.add(content[k]);
      }
      return arr;
    }
  }

  class CurrentCounter {
    final int i;
    final Object value;

    CurrentCounter(int i, Object value) {
      this.i = i;
      this.value = value;
    }
  }

}
