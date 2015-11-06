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

import de.braintags.io.vertx.pojomapper.exception.TypeHandlerException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IObjectReference;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
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

  /**
   * 
   */
  public DefaultPropertyMapper() {
  }

  @Override
  public void intoStoreObject(Object mapper, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Void>> handler) {
    ITypeHandler th = field.getTypeHandler();
    IPropertyAccessor pAcc = field.getPropertyAccessor();
    Object javaValue = pAcc.readData(mapper);

    th.intoStore(javaValue, field, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object dbValue = result.result().getResult();
        if (javaValue != null && dbValue == null) {
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
    ITypeHandler th = field.getTypeHandler();
    Object dbValue = storeObject.get(field);

    th.fromStore(dbValue, field, null, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
        return;
      }
      Object javaValue = result.result().getResult();
      handleInstanceFromStore(storeObject, mapper, javaValue, dbValue, field, handler);
    });
  }

  private void handleInstanceFromStore(IStoreObject<?> storeObject, Object mapper, Object javaValue, Object dbValue,
      IField field, Handler<AsyncResult<Void>> handler) {
    if (javaValue == null && dbValue != null) {
      Future<Void> future = Future.failedFuture(new TypeHandlerException(
          String.format("Value conversion failed for field %s: original = %s, conversion = NULL", field.getFullName(),
              String.valueOf(dbValue))));
      handler.handle(future);
      return;
    }

    try {
      if (javaValue instanceof IObjectReference) {
        storeObject.getObjectReferences().add((IObjectReference) javaValue);
      } else if (javaValue != null) {
        IPropertyAccessor pAcc = field.getPropertyAccessor();
        pAcc.writeData(mapper, javaValue);
      }
      handler.handle(Future.succeededFuture());
    } catch (Exception e) {
      LOGGER.error("", e);
      handler.handle(Future.failedFuture(e));
    }

  }
}
