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

import java.util.Collection;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;

/**
 * An abstract implementation of IPropertyMapper, which is checking the field, wether it is a single value field, an
 * Array, {@link Map} or {@link Collection} and calls the convenient methods for those
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractSubobjectMapper implements IPropertyMapper {

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
      writeMap((Map<?, ?>) javaValue, storeObject, field);
    } else if (field.isArray()) {
      writeArray((Object[]) javaValue, storeObject, field);
    } else if (!field.isSingleValue()) {
      writeCollection((Iterable<?>) javaValue, storeObject, field);
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

  public void writeMap(Map<?, ?> javaValue, IStoreObject<?> storeObject, IField field) {
    throw new UnsupportedOperationException();
  }

  public void writeArray(Object[] javaValue, IStoreObject<?> storeObject, IField field) {
    throw new UnsupportedOperationException();
  }

  public void writeCollection(Iterable<?> javaValue, IStoreObject<?> storeObject, IField field) {
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
      readMap(storeObject, field);
    } else if (field.isArray()) {
      readArray(storeObject, field);
    } else if (!field.isSingleValue()) {
      readCollection(storeObject, field);
    } else {
      readSingleValue(entity, storeObject, field, result -> {
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

  public void readMap(IStoreObject<?> storeObject, IField field) {
    throw new UnsupportedOperationException();
  }

  public void readArray(IStoreObject<?> storeObject, IField field) {
    throw new UnsupportedOperationException();
  }

  public void readCollection(IStoreObject<?> storeObject, IField field) {
    throw new UnsupportedOperationException();
  }

  public abstract void readSingleValue(Object entity, final IStoreObject<?> storeObject, final IField field,
      Handler<AsyncResult<Object>> handler);
}
