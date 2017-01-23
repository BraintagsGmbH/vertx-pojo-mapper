/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.json.typehandler.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;
import de.braintags.vertx.jomnigate.mapping.IObjectReference;
import de.braintags.vertx.jomnigate.mapping.impl.ObjectReference;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerReferenced;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * An implementation of {@link ITypeHandler} which handles Arrays which are annotated to be {@link Referenced}
 * 
 * @author Michael Remme
 * 
 */

public class ArrayTypeHandlerReferenced extends ArrayTypeHandler implements ITypeHandlerReferenced {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public ArrayTypeHandlerReferenced(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.typehandler.AbstractTypeHandler#matchesAnnotation(java.lang.annotation.Annotation)
   */
  @Override
  protected boolean matchesAnnotation(Annotation annotation) {
    return annotation != null && annotation instanceof Referenced;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    Class<?> mapperClass = cls != null ? cls : field.getType();
    if (mapperClass == null) {
      fail(new NullPointerException("undefined mapper class"), handler);
      return;
    }
    if (field.getMapper().handleReferencedRecursive()) {
      IDataStore store = field.getMapper().getMapperFactory().getDataStore();
      ObjectReference objectReference = new ObjectReference(field, source);
      resolveReferencedObject(store, objectReference, handler);
    } else {
      ObjectReference objectReference = new ObjectReference(field, source);
      success(objectReference, handler);
    }
  }

  @Override
  public void resolveReferencedObject(IDataStore store, IObjectReference reference,
      Handler<AsyncResult<ITypeHandlerResult>> handler) {
    IMapperFactory mf = store.getMapperFactory();
    IField field = reference.getField();
    IMapper subMapper = mf.getMapper(field.getSubClass());
    JsonArray jsonArray = (JsonArray) reference.getDbSource();
    if (jsonArray == null) {
      success(null, handler);
    } else if (jsonArray.isEmpty()) {
      success(Array.newInstance(field.getSubClass(), 0), handler);
    } else {
      CompositeFuture cf = resolveSubReferences(store, subMapper, field, jsonArray);
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
    final Object resultArray = Array.newInstance(field.getSubClass(), jsonArray.size());
    List<ITypeHandlerResult> results = cf.list();
    for (int i = 0; i < results.size(); i++) {
      Object javaValue = results.get(i).getResult();
      if (javaValue != null) {
        Array.set(resultArray, i, javaValue);
      }
    }
    return resultArray;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private CompositeFuture resolveSubReferences(IDataStore store, IMapper subMapper, IField field, JsonArray jsonArray) {
    List<Future> fl = new ArrayList<>();
    ObjectTypeHandlerReferenced subTypehandler = (ObjectTypeHandlerReferenced) field.getSubTypeHandler();
    for (int i = 0; i < jsonArray.size(); i++) {
      Future f = Future.future();
      subTypehandler.getReferencedObjectById(store, subMapper, jsonArray.getValue(i), f.completer());
      fl.add(i, f);
    }
    return CompositeFuture.all(fl);
  }

}
