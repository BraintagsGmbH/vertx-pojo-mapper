/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mysql.typehandler;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.json.typehandler.handler.ObjectTypeHandlerEmbedded;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mysql.dataaccess.SqlStoreObject;
import de.braintags.vertx.jomnigate.mysql.dataaccess.SqlStoreObjectFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * 
 * @author Michael Remme
 * 
 */

public class SqlObjectTypehandlerEmbedded extends ObjectTypeHandlerEmbedded {

  /**
   * @param typeHandlerFactory
   */
  public SqlObjectTypehandlerEmbedded(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.json.typehandler.handler.ObjectTypeHandlerEmbedded#fromStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object dbValue, IProperty field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> handler) {
    try {
      JsonObject jsonObject = dbValue == null ? null : new JsonObject((String) dbValue);
      super.fromStore(jsonObject, field, cls, handler);
    } catch (Exception e) {
      fail(e, handler);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.json.typehandler.handler.ObjectTypeHandlerEmbedded#intoStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void intoStore(Object embeddedObject, IProperty field, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    super.intoStore(embeddedObject, field, result -> {
      if (result.failed()) {
        handler.handle(result);
      }
      try {
        JsonObject thr = (JsonObject) result.result().getResult();
        String newResult = thr == null ? null : thr.encode();
        success(newResult, handler);
      } catch (Exception e) {
        fail(e, handler);
      }
    });
  }

  @SuppressWarnings("rawtypes")
  private void checkId(IDataStore store, Object embeddedObject, IMapper mapper, Handler<AsyncResult<Void>> handler) {
    IProperty field = mapper.getIdField().getField();
    Object id = field.getPropertyAccessor().readData(embeddedObject);
    if (id != null) {
      handler.handle(Future.succeededFuture());
    } else {
      store.getDefaultKeyGenerator().generateKey(mapper, keyResult -> {
        if (keyResult.failed()) {
          handler.handle(Future.failedFuture(keyResult.cause()));
        } else {
          field.getTypeHandler().fromStore(keyResult.result().getKey(), field, null, result -> {
            if (result.failed()) {
              handler.handle(Future.failedFuture(result.cause()));
            } else {
              Object javaValue = result.result().getResult();
              field.getPropertyAccessor().writeData(embeddedObject, javaValue);
              handler.handle(Future.succeededFuture());
            }
          });
        }
      });
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.json.typehandler.handler.ObjectTypeHandlerEmbedded#writeSingleValueAsMapper(de.
   * braintags.vertx.jomnigate.IDataStore, java.lang.Object, de.braintags.vertx.jomnigate.mapping.IMapper,
   * de.braintags.vertx.jomnigate.mapping.IField, io.vertx.core.Handler)
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  protected void writeSingleValueAsMapper(IDataStore<?, ?> store, Object embeddedObject, IMapper embeddedMapper,
      IProperty field, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    checkId(store, embeddedObject, embeddedMapper, idResult -> {
      if (idResult.failed()) {
        fail(idResult.cause(), handler);
      } else {
        ((SqlStoreObjectFactory) store.getStoreObjectFactory()).createStoreObject(embeddedMapper, embeddedObject,
            result -> {
              if (result.failed()) {
                fail(result.cause(), handler);
              } else {
                success(((SqlStoreObject) result.result()).getContainerAsJson(), handler);
              }
            });
      }
    });

  }

}
