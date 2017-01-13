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
package de.braintags.io.vertx.pojomapper.json.typehandler.handler;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
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
 * Deals all fields, which contain {@link Map} content, which are NOT annotated as {@link Referenced} or
 * {@link Embedded}
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
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (source == null) {
      success(null, resultHandler);
    } else if (((JsonArray) source).isEmpty()) {
      success(field.getMapper().getObjectFactory().createMap(field), resultHandler);
    } else {
      CompositeFuture cf = handleObjectsFromStore(field, (JsonArray) source);
      cf.setHandler(result -> {
        if (result.failed()) {
          resultHandler.handle(Future.failedFuture(result.cause()));
        } else {
          Map map = field.getMapper().getObjectFactory().createMap(field);
          for (Object entry : cf.list()) {
            map.put(((MapEntry) entry).key, ((MapEntry) entry).value);
          }
          success(map, resultHandler);
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
  @SuppressWarnings("rawtypes")
  @Override
  public final void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (source == null) {
      success(null, resultHandler);
    } else if (((Map) source).isEmpty()) {
      success(encodeResultArray(new JsonArray()), resultHandler);
    } else {
      Map map = (Map) source;
      CompositeFuture cf = encodeSubValues(map, field);
      cf.setHandler(cfh -> {
        if (cfh.failed()) {
          fail(cfh.cause(), resultHandler);
        } else {
          try {
            success(encodeResultArray(transferEncodedResults(cf)), resultHandler);
          } catch (Exception e) {
            resultHandler.handle(Future.failedFuture(e));
          }
        }
      });
    }
  }

  @SuppressWarnings("rawtypes")
  private CompositeFuture handleObjectsFromStore(IField field, JsonArray source) {
    List<Future> fl = new ArrayList<>();
    for (int i = 0; i < source.size(); i++) {
      fl.add(i, handleObjectFromStore(field, source.getJsonArray(i)));
    }
    return CompositeFuture.all(fl);
  }

  /**
   * Create one instance of the {@link Collection} and return the Future
   * 
   * @param o
   *          the object from the store
   * @param subHandler
   *          the subhandler to be used
   * @param coll
   *          the collection to be filled
   * @param field
   *          the field, where the Collection stays in
   * @param resultHandler
   *          the handler to be informed
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected Future handleObjectFromStore(IField field, JsonArray array) {
    Future f = Future.future();
    Object keyIn = array.getValue(0);
    ITypeHandler keyTypehandler = field.getMapper().getMapperFactory().getTypeHandlerFactory()
        .getTypeHandler(field.getMapKeyClass(), null);
    keyTypehandler.fromStore(keyIn, field, field.getMapKeyClass(), keyResult -> {
      if (keyResult.failed()) {
        f.fail(keyResult.cause());
      } else {
        Object valueIn = array.getValue(1);
        convertValueFromStore(valueIn, field, valueResult -> {
          if (valueResult.failed()) {
            f.fail(valueResult.cause());
          } else {
            Object javaValue = valueResult.result();
            if (javaValue != null) {
              f.complete(new MapEntry(keyResult.result().getResult(), valueResult.result()));
            } else {
              f.complete();
            }
          }
        });
      }
    });
    return f;
  }

  /**
   * Converts the value for one entry like it was coming from the datastore into the needed format for the object to be
   * filled
   * 
   * @param valueIn
   *          the value from the datastore
   * @param field
   *          the field of the {@link Map}
   * @param resultHandler
   *          the {@link Handler} to be informed
   */
  protected void convertValueFromStore(Object valueIn, IField field, Handler<AsyncResult<Object>> resultHandler) {
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

  /**
   * Transfers the results inside the {@link CompositeFuture} into a JsonArray.
   * 
   * @param cf
   *          a CompositeFuture, where the results are of type JsonArray with key / value pair
   * @return an instance which contains the encoded results of the CompositeFuture.
   */
  protected final JsonArray transferEncodedResults(CompositeFuture cf) {
    JsonArray jsonArray = new JsonArray();
    for (Object thr : cf.list()) {
      JsonArray value = (JsonArray) thr;
      if (value == null) {
        jsonArray.addNull();
      } else {
        jsonArray.add(value);
      }
    }
    return jsonArray;
  }

  @SuppressWarnings("rawtypes")
  protected CompositeFuture encodeSubValues(Map map, IField field) {
    List<Future> fl = new ArrayList<>();
    Iterator<?> it = map.entrySet().iterator();
    while (it.hasNext()) {
      fl.add(encodeSubValue(field, (Entry) it.next()));
    }
    return CompositeFuture.all(fl);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected Future encodeSubValue(IField field, Entry entry) {
    Future f = Future.future();
    ITypeHandler keyTh = getKeyTypeHandler(entry.getKey(), field);

    keyTh.intoStore(entry.getKey(), field, keyResult -> {
      if (keyResult.failed()) {
        f.fail(keyResult.cause());
      } else {
        ITypeHandler valueTh = getValueTypeHandler(entry.getValue(), field);
        valueTh.intoStore(entry.getValue(), field, valueResult -> {
          if (valueResult.failed()) {
            f.fail(valueResult.cause());
          } else {
            f.complete(new JsonArray().add(keyResult.result().getResult()).add(valueResult.result().getResult()));
          }
        });
      }
    });
    return f;
  }

  /**
   * Converts the JsonArray into an adequate format which can be stored by the datastore
   * 
   * @param result
   * @return
   */
  protected Object encodeResultArray(JsonArray result) {
    return result;
  }

  /**
   * Get the {@link ITypeHandler} which shall be used for the entry value
   * 
   * @param value
   *          the value to be written
   * @param field
   *          the field to be handled
   * @return the {@link ITypeHandler} to be used
   */
  @SuppressWarnings("rawtypes")
  protected ITypeHandler getValueTypeHandler(Object value, IField field) {
    Class valueClass = field.getSubClass();
    if (valueClass == null || valueClass == Object.class)
      valueClass = value.getClass();
    return getSubTypeHandler(valueClass, field.getEmbedRef());
  }

  /**
   * Get the {@link ITypeHandler} which shall be used for the entry key
   * 
   * @param value
   *          the value to be written
   * @param field
   *          the field to be handled
   * @return the {@link ITypeHandler} to be used
   */
  @SuppressWarnings("rawtypes")
  public ITypeHandler getKeyTypeHandler(Object value, IField field) {
    Class keyClass = field.getMapKeyClass();
    if (keyClass == null || keyClass == Object.class)
      keyClass = value.getClass();
    return getSubTypeHandler(keyClass, null);
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
