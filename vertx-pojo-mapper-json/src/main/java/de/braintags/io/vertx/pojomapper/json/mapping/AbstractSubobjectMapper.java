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
package de.braintags.io.vertx.pojomapper.json.mapping;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.json.dataaccess.JsonStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import de.braintags.io.vertx.util.Size;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * An abstract implementation of IPropertyMapper, which is checking the field, wether it is a single value field, an
 * Array, {@link Map} or {@link Collection} and calls the convenient methods for those
 * 
 * @author Michael Remme
 * @deprecated removed soon
 * 
 */

@Deprecated
public abstract class AbstractSubobjectMapper implements IPropertyMapper {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSubobjectMapper.class);

  /**
   * 
   */
  public AbstractSubobjectMapper() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper#readForStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void readForStore(Object entity, IField field, Handler<AsyncResult<Object>> handler) {
    JsonStoreObject jsto = new JsonStoreObject(field.getMapper(), entity);
    intoStoreObject(entity, jsto, field, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
        return;
      }
      handler.handle(Future.succeededFuture(jsto.get(field)));
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper#intoStoreObject(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IStoreObject, de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public void intoStoreObject(Object entity, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    IPropertyAccessor pAcc = field.getPropertyAccessor();
    Object javaValue = pAcc.readData(entity);
    if (javaValue == null) {
      handler.handle(Future.succeededFuture());
      return;
    }
    if (field.isMap()) {
      writeMap((Map<?, ?>) javaValue, storeObject, field, handler);
    } else if (field.isArray()) {
      writeArray((Object[]) javaValue, storeObject, field, handler);
    } else if (!field.isSingleValue()) {
      writeCollection((Iterable<?>) javaValue, storeObject, field, handler);
    } else {
      writeSingleValue(javaValue, storeObject, field, result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          storeObject.put(field, result.result());
          handler.handle(Future.succeededFuture());
        }
      });
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper#fromStoreObject(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IStoreObject, de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public void fromStoreObject(Object entity, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    if (field.isMap()) {
      readMap(entity, storeObject, field, handler);
    } else if (field.isArray()) {
      readArray(entity, storeObject, field, handler);
    } else if (!field.isSingleValue()) {
      readCollection(entity, storeObject, field, handler);
    } else {
      Object dbValue = storeObject.get(field);
      IPropertyAccessor pAcc = field.getPropertyAccessor();
      if (dbValue == null) {
        pAcc.writeData(entity, null);
        handler.handle(Future.succeededFuture());
        return;
      }

      readSingleValue(storeObject, dbValue, field, null, result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          Object javaValue = result.result();
          if (javaValue != null)
            pAcc.writeData(entity, javaValue);
          handler.handle(Future.succeededFuture());
        }
      });
    }
  }

  /**
   * Write action for those fields, where an Array is marked as {@link Referenced}
   * 
   * @param javaValues
   *          the array to be stored
   * @param storeObject
   *          the storeobject
   * @param field
   *          the field
   * @param handler
   *          the handler to be called
   */
  protected void writeArray(Object[] javaValues, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    if (javaValues == null || javaValues.length == 0)
      handler.handle(Future.succeededFuture());
    ErrorObject<Void> errorObject = new ErrorObject<Void>(handler);
    CounterObject co = new CounterObject(javaValues.length);
    Object[] resultArray = new Object[javaValues.length];
    for (int i = 0; i < javaValues.length; i++) {
      // trying to write the array in the order like it is
      CurrentCounter cc = new CurrentCounter(i, javaValues[i]);
      writeSingleValue(cc.value, storeObject, field,
          result -> doWriteSingleValue(result, errorObject, co, resultArray, cc, storeObject, field, handler));
      if (errorObject.isError())
        return;
    }
  }

  private void doWriteSingleValue(AsyncResult<Object> result, ErrorObject<Void> errorObject, CounterObject co,
      Object[] resultArray, CurrentCounter cc, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    if (result.failed()) {
      LOGGER.info("failed", result.cause());
      errorObject.setThrowable(result.cause());
    } else {
      resultArray[cc.i] = result.result();
      LOGGER.info("success write: " + cc.value.toString() + " into " + cc.i);
      if (co.reduce()) {
        JsonArray arr = new JsonArray();
        for (int k = 0; k < resultArray.length; k++) {
          arr.add(resultArray[k]);
        }
        storeObject.put(field, arr);
        handler.handle(Future.succeededFuture());
      }
    }

  }

  /**
   * Read action for those fields, where an Array is marked as {@link Referenced}
   * 
   * @param entity
   *          the entity to be filled
   * @param storeObject
   *          the storeobject from the datastore
   * @param field
   *          the field
   * @param handler
   *          the handler to be called
   */
  protected void readArray(Object entity, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    readInternal(storeObject, field, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        IPropertyAccessor pAcc = field.getPropertyAccessor();
        pAcc.writeData(entity, result.result());
        handler.handle(Future.succeededFuture());
      }
    });
  }

  protected void readInternal(IStoreObject<?> storeObject, IField field, Handler<AsyncResult<Object>> handler) {
    JsonArray jsonArray = (JsonArray) storeObject.get(field);
    final Object resultArray = Array.newInstance(field.getSubClass(), jsonArray.size());
    if (jsonArray == null || jsonArray.isEmpty()) {
      handler.handle(Future.succeededFuture(resultArray));
      return;
    }
    ErrorObject<Object> errorObject = new ErrorObject<Object>(handler);
    CounterObject co = new CounterObject(jsonArray.size());
    int counter = 0;
    for (Object jo : jsonArray) {
      CurrentCounter cc = new CurrentCounter(counter++, jo);
      readSingleValue(storeObject, cc.value, field, field.getSubClass(), result -> {
        if (result.failed()) {
          LOGGER.info("failed", result.cause());
          errorObject.setThrowable(result.cause());
          return;
        } else {
          Object javaValue = result.result();
          LOGGER.info("success read: " + javaValue.toString() + " into " + cc.i);
          if (javaValue != null)
            Array.set(resultArray, cc.i, javaValue);
          if (co.reduce()) {
            handler.handle(Future.succeededFuture(resultArray));
          }
        }
      });
      if (errorObject.isError())
        return;
    }
  }

  /**
   * Read action for those fields, where a {@link Map} is marked as {@link Referenced}
   * 
   * @param entity
   *          the entity to be filled
   * @param storeObject
   *          the storeobject from the datastore
   * @param field
   *          the field
   * @param handler
   *          the handler to be called
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void readMap(Object entity, IStoreObject<?> storeObject, IField field, Handler<AsyncResult<Void>> handler) {
    JsonArray jsonArray = (JsonArray) storeObject.get(field);
    if (jsonArray == null || jsonArray.isEmpty())
      handler.handle(Future.succeededFuture());
    ErrorObject<Void> errorObject = new ErrorObject<Void>(handler);
    CounterObject co = new CounterObject(jsonArray.size());
    final MapEntry[] resultArray = new MapEntry[jsonArray.size()];
    int counter = 0;

    for (Object jo : jsonArray) {
      CurrentCounter cc = new CurrentCounter(counter++, jo);
      Object keyIn = ((JsonArray) cc.value).getValue(0);
      ITypeHandler th = field.getMapper().getMapperFactory().getDataStore().getTypeHandlerFactory()
          .getTypeHandler(field.getMapKeyClass(), field.getEmbedRef());

      th.fromStore(keyIn, field, field.getMapKeyClass(), keyResult -> {
        if (keyResult.failed()) {
          LOGGER.info("failed", keyResult.cause());
          errorObject.setThrowable(keyResult.cause());
        } else {
          Object valueIn = ((JsonArray) cc.value).getValue(1);
          readSingleValue(storeObject, valueIn, field, field.getSubClass(), valueResult -> {
            if (valueResult.failed()) {
              LOGGER.info("failed", valueResult.cause());
              errorObject.setThrowable(valueResult.cause());
            } else {
              Object javaValue = valueResult.result();
              LOGGER.info("success read: " + javaValue.toString() + " into " + cc.i);
              if (javaValue != null) {
                resultArray[cc.i] = new MapEntry(keyResult.result().getResult(), valueResult.result());
              }

              if (co.reduce()) {
                Map map = field.getMapper().getObjectFactory().createMap(field);
                for (int i = 0; i < resultArray.length; i++) {
                  map.put(resultArray[i].key, resultArray[i].value);
                }
                IPropertyAccessor pAcc = field.getPropertyAccessor();
                pAcc.writeData(entity, map);
                handler.handle(Future.succeededFuture());
              }
            }
          });
        }
      });

      if (errorObject.isError())
        return;
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

  /**
   * Write action for those fields, where a {@link Map} is marked as {@link Referenced} The key of each entry in the map
   * is written by using a suitable {@link ITypeHandler}, the value is resolved to its reference
   * 
   * @param javaValues
   *          the array to be stored
   * @param storeObject
   *          the storeobject
   * @param field
   *          the field
   * @param handler
   *          the handler to be called
   */
  @SuppressWarnings("rawtypes")
  protected void writeMap(Map<?, ?> map, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    int size = map == null ? 0 : map.size();
    if (size == 0)
      handler.handle(Future.succeededFuture());
    ErrorObject<Void> errorObject = new ErrorObject<Void>(handler);
    CounterObject co = new CounterObject(size);
    JsonArray[] resultArray = new JsonArray[size];
    Iterator<?> it = map.entrySet().iterator();
    int counter = 0;
    while (it.hasNext()) {
      // trying to write the array in the order like it is
      Entry entry = (Entry) it.next();
      CurrentCounter cc = new CurrentCounter(counter++, entry);
      ITypeHandler th = field.getMapper().getMapperFactory().getDataStore().getTypeHandlerFactory()
          .getTypeHandler(field.getMapKeyClass(), field.getEmbedRef());

      th.intoStore(((Entry) cc.value).getKey(), field, keyResult -> {
        if (keyResult.failed()) {
          LOGGER.info("failed", keyResult.cause());
          errorObject.setThrowable(keyResult.cause());
        } else {
          writeSingleValue(((Entry) cc.value).getValue(), storeObject, field, valueResult -> {
            if (valueResult.failed()) {
              LOGGER.info("failed", valueResult.cause());
              errorObject.setThrowable(keyResult.cause());
            } else {
              resultArray[cc.i] = new JsonArray().add(keyResult.result().getResult()).add(valueResult.result());
              LOGGER.info("success write: " + cc.value.toString() + " into " + cc.i);
              if (co.reduce()) {
                JsonArray arr = new JsonArray();
                for (int k = 0; k < resultArray.length; k++) {
                  arr.add(resultArray[k]);
                }
                storeObject.put(field, arr);
                handler.handle(Future.succeededFuture());
              }
            }
          });
        }
      });
      if (errorObject.isError())
        return;
    }
  }

  /**
   * Write action for those fields, where an {@link Iterable} is marked as {@link Referenced}
   * 
   * @param javaValues
   *          the array to be stored
   * @param storeObject
   *          the storeobject
   * @param field
   *          the field
   * @param handler
   *          the handler to be called
   */
  protected void writeCollection(Iterable<?> iterable, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    int size = Size.size(iterable);
    if (size == 0)
      handler.handle(Future.succeededFuture());
    ErrorObject<Void> errorObject = new ErrorObject<Void>(handler);
    CounterObject co = new CounterObject(size);
    Object[] resultArray = new Object[size];
    Iterator<?> it = iterable.iterator();
    int counter = 0;
    while (it.hasNext()) {
      // trying to write the array in the order like it is
      Object javaValue = it.next();
      CurrentCounter cc = new CurrentCounter(counter++, javaValue);
      writeSingleValue(cc.value, storeObject, field,
          result -> doWriteSingleValue(result, errorObject, co, resultArray, cc, storeObject, field, handler));
      if (errorObject.isError())
        return;
    }
  }

  /**
   * Read action for those fields, where an {@link Iterable} is marked as {@link Referenced}
   * 
   * @param entity
   *          the entity to be filled
   * @param storeObject
   *          the storeobject from the datastore
   * @param field
   *          the field
   * @param handler
   *          the handler to be called
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void readCollection(Object entity, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    readInternal(storeObject, field, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object resultArray = result.result();
        Collection coll = field.getMapper().getObjectFactory().createCollection(field);
        for (int i = 0; i < Array.getLength(resultArray); i++) {
          coll.add(Array.get(resultArray, i));
        }
        IPropertyAccessor pAcc = field.getPropertyAccessor();
        pAcc.writeData(entity, coll);
        handler.handle(Future.succeededFuture());
      }
    });
  }

  /**
   * Write the reference to a child record into the field. This method expects, that the child is saved before, so that
   * it got an id already
   * 
   * @param referencedObject
   *          the child instance to be handled
   * @param storeObject
   *          the {@link IStoreObject}
   * @param field
   *          the field, where the reference is stored inside
   * @param handler
   *          the handler to be called
   */
  protected abstract void writeSingleValue(final Object referencedObject, final IStoreObject<?> storeObject,
      final IField field, Handler<AsyncResult<Object>> handler);

  /**
   * Generate a java value from the given dbValue
   * 
   * @param storeObject
   *          the storeobject from the datastore
   * @param dbValue
   *          the Object like read from the datastore
   * @param field
   *          a field information, which will be used to handle the mapping. Can be null, if mapperClass is defined
   * @param mapperClass
   *          optionally a mapper class
   * @param handler
   *          the handler to be recalled
   */
  protected abstract void readSingleValue(IStoreObject<?> storeObject, Object dbValue, final IField field,
      Class<?> mapperClass, Handler<AsyncResult<Object>> handler);

  class CurrentCounter {
    int i;
    Object value;

    CurrentCounter(int i, Object value) {
      this.i = i;
      this.value = value;
    }
  }

}
