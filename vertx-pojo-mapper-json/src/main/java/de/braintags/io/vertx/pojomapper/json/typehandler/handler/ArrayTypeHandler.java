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

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
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
    if (jsonArray == null) {
      success(null, handler);
    } else if (jsonArray.isEmpty()) {
      success(Array.newInstance(field.getSubClass(), 0), handler);
    } else {
      CompositeFuture cf = CompositeFuture.all(extractSubValues(field, jsonArray));
      cf.setHandler(result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          final Object resultArray = transferValues(field, jsonArray, cf);
          success(resultArray, handler);
        }
      });
    }
  }

  /**
   * @param field
   * @param jsonArray
   * @param cf
   * @return
   */
  private Object transferValues(IField field, JsonArray jsonArray, CompositeFuture cf) {
    List<ITypeHandlerResult> thl = cf.list();
    final Object resultArray = Array.newInstance(field.getSubClass(), jsonArray.size());
    for (int i = 0; i < thl.size(); i++) {
      Object javaValue = thl.get(i).getResult();
      if (javaValue != null) {
        Array.set(resultArray, i, javaValue);
      }
    }
    return resultArray;
  }

  @SuppressWarnings("rawtypes")
  private List<Future> extractSubValues(IField field, JsonArray jsonArray) {
    List<Future> fl = new ArrayList<>(jsonArray.size());
    ITypeHandler subTypehandler = field.getSubTypeHandler();
    for (int i = 0; i < jsonArray.size(); i++) {
      Object jo = jsonArray.getValue(i);
      fl.add(i, extractSubValue(field, subTypehandler, jo));
    }
    return fl;
  }

  private Future<ITypeHandlerResult> extractSubValue(IField field, ITypeHandler subTypeHandler, Object jo) {
    Future<ITypeHandlerResult> f = Future.future();
    subTypeHandler.fromStore(jo, field, field.getSubClass(), f.completer());
    return f;
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
    if (javaValues == null) {
      success(null, handler);
    } else if (length == 0) {
      success(new JsonArray(), handler);
    } else {
      ITypeHandler subTypehandler = field.getSubTypeHandler();
      CompositeFuture cf = writeEntries(subTypehandler, field, javaValues, length);
      cf.setHandler(cfResult -> {
        if (cfResult.failed()) {
          handler.handle(Future.failedFuture(cfResult.cause()));
        } else {
          success(createJsonResult(cf), handler);
        }
      });
    }
  }

  private JsonArray createJsonResult(CompositeFuture cf) {
    JsonArray arr = new JsonArray();
    List<ITypeHandlerResult> thl = cf.list();
    for (int k = 0; k < thl.size(); k++) {
      Object value = thl.get(k).getResult();
      if (value != null) {
        arr.add(value);
      } else {
        arr.addNull();
      }
    }
    return arr;
  }

  @SuppressWarnings("rawtypes")
  private CompositeFuture writeEntries(ITypeHandler subTypehandler, IField field, Object javaValues, int length) {
    List<Future> fl = new ArrayList<>(length);
    for (int i = 0; i < length; i++) {
      Object value = Array.get(javaValues, i);
      fl.add(i, writeEntry(subTypehandler, field, value));
    }
    return CompositeFuture.all(fl);
  }

  private Future<ITypeHandlerResult> writeEntry(ITypeHandler subTypehandler, IField field, Object javaValue) {
    Future<ITypeHandlerResult> f = Future.future();
    subTypehandler.intoStore(javaValue, field, f.completer());
    return f;
  }

}
