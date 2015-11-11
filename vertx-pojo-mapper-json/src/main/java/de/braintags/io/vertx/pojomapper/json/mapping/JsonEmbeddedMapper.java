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

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.mapping.IEmbeddedMapper;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IObjectReference;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * Implementation of {@link IEmbeddedMapper} which is used to store subobjects embedded in the field of their parent
 * instance
 * 
 * @author Michael Remme
 * @deprecated removed soon
 * 
 */

@Deprecated
public class JsonEmbeddedMapper extends AbstractSubobjectMapper implements IEmbeddedMapper {

  /**
   * 
   */
  public JsonEmbeddedMapper() {
  }

  @Override
  public void writeSingleValue(Object embeddedObject, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Object>> handler) {
    IDataStore store = field.getMapper().getMapperFactory().getDataStore();
    if (store.getMapperFactory().isMapper(embeddedObject.getClass())) {
      writeSingleValueAsMapper(store, embeddedObject, storeObject, field, handler);
    } else {
      writeSingleValueAsTypehandler(store, embeddedObject, storeObject, field, handler);
    }
  }

  protected void writeSingleValueAsTypehandler(IDataStore store, Object embeddedObject, IStoreObject<?> storeObject,
      IField field, Handler<AsyncResult<Object>> handler) {
    ITypeHandler th = store.getTypeHandlerFactory().getTypeHandler(embeddedObject.getClass(), field.getEmbedRef());
    th.intoStore(embeddedObject, field, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object jo = result.result().getResult();
        handler.handle(Future.succeededFuture(jo));
      }
    });
  }

  protected void writeSingleValueAsMapper(IDataStore store, Object embeddedObject, IStoreObject<?> storeObject,
      IField field, Handler<AsyncResult<Object>> handler) {
    IMapper mapper = store.getMapperFactory().getMapper(embeddedObject.getClass());
    store.getStoreObjectFactory().createStoreObject(mapper, embeddedObject, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        JsonObject jo = (JsonObject) result.result().getContainer();
        handler.handle(Future.succeededFuture(jo));
      }
    });
  }

  @Override
  public void readSingleValue(IStoreObject<?> storeObject, Object dbValue, IField field, Class<?> mapperClass,
      Handler<AsyncResult<Object>> handler) {
    IDataStore store = field.getMapper().getMapperFactory().getDataStore();
    Class<?> internalMapperClass = mapperClass != null ? mapperClass : field.getType();
    if (store.getMapperFactory().isMapper(internalMapperClass)) {
      readSingleValueAsMapper(store, internalMapperClass, dbValue, handler);
    } else {
      readSingleValueAsTypeHandler(store, field, internalMapperClass, dbValue, handler);
    }
  }

  protected void readSingleValueAsMapper(IDataStore store, Class<?> internalMapperClass, Object dbValue,
      Handler<AsyncResult<Object>> handler) {
    IMapper mapper = store.getMapperFactory().getMapper(internalMapperClass);
    store.getStoreObjectFactory().createStoreObject(dbValue, mapper, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object jo = result.result().getEntity();
        handler.handle(Future.succeededFuture(jo));
      }
    });
  }

  protected void readSingleValueAsTypeHandler(IDataStore store, IField field, Class<?> internalMapperClass,
      Object dbValue, Handler<AsyncResult<Object>> handler) {
    ITypeHandler th = store.getTypeHandlerFactory().getTypeHandler(internalMapperClass, field.getEmbedRef());
    th.fromStore(dbValue, field, internalMapperClass, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object jo = result.result().getResult();
        handler.handle(Future.succeededFuture(jo));
      }
    });
  }

  @Override
  public void fromObjectReference(Object entity, IObjectReference reference, Handler<AsyncResult<Void>> handler) {
    handler.handle(Future.failedFuture(new UnsupportedOperationException()));
  }

}
