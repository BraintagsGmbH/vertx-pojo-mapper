/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mapping.impl;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.exception.TypeHandlerException;
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.IObjectReference;
import de.braintags.vertx.jomnigate.mapping.IPropertyAccessor;
import de.braintags.vertx.jomnigate.mapping.IPropertyMapper;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerReferenced;
import de.braintags.vertx.util.exception.PropertyAccessException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class DefaultPropertyMapper implements IPropertyMapper {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(DefaultPropertyMapper.class);

  @Override
  public <T> void intoStoreObject(T mapper, IStoreObject<T, ? > storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    ITypeHandler th = field.getTypeHandler();
    IPropertyAccessor pAcc = field.getPropertyAccessor();
    Object javaValue = pAcc.readData(mapper);
    if (field.getEncoder() != null) {
      javaValue = field.getEncoder().encode((CharSequence) javaValue);
      pAcc.writeData(mapper, javaValue);
    }
    intoStoreObject(storeObject, field, th, javaValue, handler);
  }

  /**
   * Store a java value into the StoreObject by using the defined typehandler
   * 
   * @param storeObject
   *          the storeObject to place the converted value into
   * @param field
   *          the field to be handled
   * @param th
   *          the {@link ITypeHandler} to be used to convert
   * @param javaValue
   *          the value to convert
   * @param handler
   *          the handler to be informed
   */
  public static <T> void intoStoreObject(IStoreObject<T, ?> storeObject, IField field, ITypeHandler th,
      Object javaValue, Handler<AsyncResult<Void>> handler) {
    LOGGER.debug(
        "starting intoStoreObject for field " + field.getFullName() + " with typehandler " + th.getClass().getName());
    th.intoStore(javaValue, field, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object dbValue = result.result().getResult();
        LOGGER.debug("received result from typehandler: " + String.valueOf(dbValue));
        storeObject.put(field, dbValue);
        handler.handle(Future.succeededFuture());
      }
    });
  }

  @Override
  public <T> void readForStore(T mapper, IField field, Handler<AsyncResult<Object>> handler) {
    ITypeHandler th = field.getTypeHandler();
    IPropertyAccessor pAcc = field.getPropertyAccessor();
    Object javaValue = pAcc.readData(mapper);

    th.intoStore(javaValue, field, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object dbValue = result.result().getResult();
        if (javaValue != null && dbValue == null) {
          Future<Object> future = Future.failedFuture(new TypeHandlerException(
              String.format("Value conversion failed: original = %s, conversion = NULL", String.valueOf(javaValue))));
          handler.handle(future);
          return;
        }
        handler.handle(Future.succeededFuture(dbValue));
      }
    });
  }

  @Override
  public <T> void fromStoreObject(T mapper, IStoreObject<T, ?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    ITypeHandler th = field.getTypeHandler();
    LOGGER.debug(
        "starting fromStoreObject for field " + field.getFullName() + " with typehandler " + th.getClass().getName());
    Object dbValue = storeObject.get(field);

    th.fromStore(dbValue, field, null, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object javaValue = result.result().getResult();
        LOGGER.debug("received result from typehandler: " + String.valueOf(javaValue));
        handleInstanceFromStore(storeObject, mapper, javaValue, dbValue, field, handler);
      }
    });

  }

  private <T> void handleInstanceFromStore(IStoreObject<T, ? > storeObject, T mapper, Object javaValue, Object dbValue,
      IField field, Handler<AsyncResult<Void>> handler) {
    try {
      if (javaValue instanceof IObjectReference) {
        storeObject.getObjectReferences().add((IObjectReference) javaValue);
        LOGGER.debug("added ObjectReference");
      } else {
        IPropertyAccessor pAcc = field.getPropertyAccessor();
        pAcc.writeData(mapper, javaValue);
        LOGGER.debug("writing data");
      }
      handler.handle(Future.succeededFuture());
    } catch (PropertyAccessException e) {
      LOGGER.error("", e);
      handler.handle(Future.failedFuture(e));
    }
  }

  @Override
  public void fromObjectReference(Object entity, IObjectReference reference, Handler<AsyncResult<Void>> handler) {
    LOGGER.debug("starting fromObjectReference");
    IDataStore store = reference.getField().getMapper().getMapperFactory().getDataStore();
    ITypeHandlerReferenced th = (ITypeHandlerReferenced) reference.getField().getTypeHandler();
    Object dbValue = reference.getDbSource();
    IField field = reference.getField();
    if (dbValue == null) { // nothing to work with? Must null be set?
      LOGGER.debug("nothing to do here - finished");
      handler.handle(Future.succeededFuture());
    } else {
      LOGGER.debug("resolving referenced object");
      th.resolveReferencedObject(store, reference, result -> {
        if (result.failed()) {
          handler.handle(Future.failedFuture(result.cause()));
          return;
        }
        Object javaValue = result.result().getResult();
        LOGGER.debug("resolved the obejct from datastore, now handling from store");
        handleInstanceFromStore(null, entity, javaValue, dbValue, field, handler);
      });
    }
  }
}
