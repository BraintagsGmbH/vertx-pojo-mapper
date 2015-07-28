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

package de.braintags.io.vertx.pojomapper.mapping.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import de.braintags.io.vertx.util.ReflectionUtil;

/**
 * An abstract implementation of IPropertyMapper, which is checking the field, wether it is a single value field, an
 * Array, {@link Map} or {@link Collection} and calls the convenient methods for those
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractSubobjectMapper implements IPropertyMapper {
  private static final Logger logger = LoggerFactory.getLogger(AbstractSubobjectMapper.class);

  /**
   * 
   */
  public AbstractSubobjectMapper() {
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
    if (javaValue == null)
      return;
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
      readMap(storeObject, field, handler);
    } else if (field.isArray()) {
      readArray(entity, storeObject, field, result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          handler.handle(Future.succeededFuture());
        }
      });
    } else if (!field.isSingleValue()) {
      readCollection(storeObject, field, handler);
    } else {
      Object dbValue = storeObject.get(field);
      readSingleValue(dbValue, field, null, result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
        } else {
          IPropertyAccessor pAcc = field.getPropertyAccessor();
          Object javaValue = result.result();
          if (javaValue != null)
            pAcc.writeData(entity, javaValue);
          handler.handle(Future.succeededFuture());
        }
      });
    }
  }

  public void writeArray(Object[] javaValues, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    if (javaValues == null || javaValues.length == 0)
      handler.handle(Future.succeededFuture());
    logger.info("writing array");
    ErrorObject<Void> errorObject = new ErrorObject<Void>();
    CounterObject co = new CounterObject(javaValues.length);
    Object[] resultArray = new Object[javaValues.length];
    for (int i = 0; i < javaValues.length; i++) {
      // trying to write the array in the order like it is
      logger.info("writing array entry " + co.getCount());
      CurrentCounter cc = new CurrentCounter(i, javaValues[i]);
      writeSingleValue(cc.javaValue, storeObject, field, result -> {
        if (result.failed()) {
          logger.info("failed");
          errorObject.setThrowable(result.cause());
        } else {
          resultArray[cc.i] = result.result();
          logger.info("success write: " + cc.javaValue.toString() + " into " + cc.i);
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
      if (errorObject.handleError(handler))
        return;
    }

  }

  class CurrentCounter {
    int i;
    Object javaValue;

    CurrentCounter(int i, Object javaValue) {
      this.i = i;
      this.javaValue = javaValue;
    }
  }

  public void readArray(Object entity, IStoreObject<?> storeObject, IField field, Handler<AsyncResult<Void>> handler) {
    logger.info("reading array");
    JsonArray jsonArray = (JsonArray) storeObject.get(field);
    if (jsonArray == null || jsonArray.isEmpty())
      handler.handle(Future.succeededFuture());
    ErrorObject<Void> errorObject = new ErrorObject<Void>();
    CounterObject co = new CounterObject(jsonArray.size());
    List<Object> resultList = new ArrayList<Object>();
    for (Object jo : jsonArray) {
      logger.info("reading array entry " + co.getCount());
      readSingleValue(jo, field, field.getSubClass(), result -> {
        if (result.failed()) {
          logger.info("failed");
          errorObject.setThrowable(result.cause());
        } else {
          Object javaValue = result.result();
          logger.info("success read: " + javaValue.toString());
          if (javaValue != null)
            resultList.add(javaValue);
          if (co.reduce()) {
            Object o = ReflectionUtil.convertToArray(field.getSubClass(), resultList);
            IPropertyAccessor pAcc = field.getPropertyAccessor();
            pAcc.writeData(entity, o);
            handler.handle(Future.succeededFuture());
          }
        }
      });
      if (errorObject.handleError(handler))
        return;
    }

  }

  public void writeMap(Map<?, ?> javaValue, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    throw new UnsupportedOperationException();
  }

  public void writeCollection(Iterable<?> javaValue, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    throw new UnsupportedOperationException();
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
  public abstract void writeSingleValue(final Object referencedObject, final IStoreObject<?> storeObject,
      final IField field, Handler<AsyncResult<Object>> handler);

  public void readMap(IStoreObject<?> storeObject, IField field, Handler<AsyncResult<Void>> handler) {
    throw new UnsupportedOperationException();
  }

  public void readCollection(IStoreObject<?> storeObject, IField field, Handler<AsyncResult<Void>> handler) {
    throw new UnsupportedOperationException();
  }

  /**
   * Generate a java value from the given dbValue
   * 
   * @param dbValue
   *          the Object like read from the datastore
   * @param field
   *          a field information, which will be used to handle the mapping. Can be null, if mapperClass is defined
   * @param mapperClass
   *          optionally a mapper class
   * @param handler
   *          the handler to be recalled
   */
  public abstract void readSingleValue(Object dbValue, final IField field, Class<?> mapperClass,
      Handler<AsyncResult<Object>> handler);
}
