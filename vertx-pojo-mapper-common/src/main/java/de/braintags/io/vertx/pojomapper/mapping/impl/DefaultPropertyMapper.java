/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mapping.impl;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.exception.PropertyAccessException;
import de.braintags.io.vertx.pojomapper.exception.TypeHandlerException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IObjectReference;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerReferenced;
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
  public void intoStoreObject(Object mapper, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    ITypeHandler th = field.getTypeHandler();
    IPropertyAccessor pAcc = field.getPropertyAccessor();
    Object javaValue = pAcc.readData(mapper);

    if (javaValue == null) {
      handler.handle(Future.succeededFuture());
      return;
    }

    th.intoStore(javaValue, field, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object dbValue = result.result().getResult();
        if (javaValue != null && javaValue.hashCode() != 0 && dbValue == null) {
          Future<Void> future = Future.failedFuture(new TypeHandlerException(
              String.format("Value conversion failed: original = %s, conversion = NULL", String.valueOf(javaValue))));
          handler.handle(future);
          return;
        }
        if (dbValue != null)
          storeObject.put(field, dbValue);
        handler.handle(Future.succeededFuture());
      }
      return;
    });
  }

  @Override
  public void readForStore(Object mapper, IField field, Handler<AsyncResult<Object>> handler) {
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
  public void fromStoreObject(Object mapper, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    LOGGER.debug("starting fromStoreObject for field " + field.getFullName());
    ITypeHandler th = field.getTypeHandler();
    Object dbValue = storeObject.get(field);
    if (dbValue == null) { // nothing to work with? Must null be set?
      LOGGER.debug("value is null - nothing to do");
      handler.handle(Future.succeededFuture());
    } else {
      LOGGER.debug("fetching result from typehandler");
      try {
        th.fromStore(dbValue, field, null, result -> {
          if (result.failed()) {
            handler.handle(Future.failedFuture(result.cause()));
            return;
          }
          Object javaValue = result.result().getResult();
          handleInstanceFromStore(storeObject, mapper, javaValue, dbValue, field, handler);
        });
      } catch (Exception e) {
        handler.handle(Future.failedFuture(
            new PropertyAccessException("Error with reading from store in field " + field.getFullName(), e)));
      }
    }
  }

  private void handleInstanceFromStore(IStoreObject<?> storeObject, Object mapper, Object javaValue, Object dbValue,
      IField field, Handler<AsyncResult<Void>> handler) {
    try {
      if (javaValue instanceof IObjectReference) {
        storeObject.getObjectReferences().add((IObjectReference) javaValue);
        LOGGER.debug("added ObjectReference");
      } else if (javaValue != null) {
        IPropertyAccessor pAcc = field.getPropertyAccessor();
        pAcc.writeData(mapper, javaValue);
        LOGGER.debug("writing data");
      }
      handler.handle(Future.succeededFuture());
    } catch (Exception e) {
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
