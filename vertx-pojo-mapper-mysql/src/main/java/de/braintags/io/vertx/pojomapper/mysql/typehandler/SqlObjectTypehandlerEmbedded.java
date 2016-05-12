/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mysql.typehandler;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlStoreObject;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlStoreObjectFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
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
   * de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectTypeHandlerEmbedded#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object dbValue, IField field, Class<?> cls, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    try {
      JsonObject jsonObject = new JsonObject((String) dbValue);
      super.fromStore(jsonObject, field, cls, handler);
    } catch (Exception e) {
      fail(e, handler);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectTypeHandlerEmbedded#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void intoStore(Object embeddedObject, IField field, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    super.intoStore(embeddedObject, field, result -> {
      if (result.failed()) {
        handler.handle(result);
      }
      try {
        String newResult = ((JsonObject) result.result().getResult()).encode();
        success(newResult, handler);
      } catch (Exception e) {
        fail(e, handler);
      }
    });
  }

  @Override
  protected void writeSingleValueAsMapper(IDataStore store, Object embeddedObject, IField field,
      Handler<AsyncResult<ITypeHandlerResult>> handler) {
    IMapper mapper = store.getMapperFactory().getMapper(embeddedObject.getClass());
    ((SqlStoreObjectFactory) store.getMapperFactory().getStoreObjectFactory()).createStoreObject(mapper, embeddedObject,
        result -> {
          if (result.failed()) {
            fail(result.cause(), handler);
          } else {
            success(((SqlStoreObject) result.result()).getContainerAsJson(), handler);
          }
        });
  }

}
