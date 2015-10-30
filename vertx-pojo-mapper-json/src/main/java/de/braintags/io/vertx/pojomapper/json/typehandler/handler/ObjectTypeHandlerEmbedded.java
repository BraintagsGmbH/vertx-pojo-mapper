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
package de.braintags.io.vertx.pojomapper.json.typehandler.handler;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * Deals all fields, which are instances of Object and which are annotated as {@link Embedded}
 * 
 * @author Michael Remme
 * 
 */

public class ObjectTypeHandlerEmbedded extends ObjectTypeHandler {

  /**
   * @param typeHandlerFactory
   */
  public ObjectTypeHandlerEmbedded(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.json.typehandler.handler.CollectionTypeHandler#matchAnnotation(de.braintags.io.
   * vertx.pojomapper.mapping.IField)
   */
  @Override
  protected short matchAnnotation(IField field) {
    if (field.hasAnnotation(Embedded.class))
      return MATCH_MINOR;
    return MATCH_NONE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.json.typehandler.handler.ArrayTypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object dbValue, IField field, Class<?> cls, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    IDataStore store = field.getMapper().getMapperFactory().getDataStore();
    Class<?> internalMapperClass = cls != null ? cls : field.getType();
    if (store.getMapperFactory().isMapper(internalMapperClass)) {
      readSingleValueAsMapper(store, internalMapperClass, dbValue, handler);
    } else {
      fail(new MappingException("Embedded should be used for mappable pojos only: " + internalMapperClass.getName()),
          handler);
    }
  }

  protected void readSingleValueAsMapper(IDataStore store, Class<?> internalMapperClass, Object dbValue,
      Handler<AsyncResult<ITypeHandlerResult>> handler) {
    IMapper mapper = store.getMapperFactory().getMapper(internalMapperClass);
    store.getStoreObjectFactory().createStoreObject(dbValue, mapper, result -> {
      if (result.failed()) {
        fail(result.cause(), handler);
      } else {
        Object jo = result.result().getEntity();
        success(jo, handler);
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.json.typehandler.handler.ArrayTypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void intoStore(Object embeddedObject, IField field, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    IDataStore store = field.getMapper().getMapperFactory().getDataStore();
    if (store.getMapperFactory().isMapper(embeddedObject.getClass())) {
      writeSingleValueAsMapper(store, embeddedObject, field, handler);
    } else {
      fail(new MappingException(
          "Embedded should be used for mappable pojos only: " + embeddedObject.getClass().getName()), handler);
    }
  }

  protected void writeSingleValueAsMapper(IDataStore store, Object embeddedObject, IField field,
      Handler<AsyncResult<ITypeHandlerResult>> handler) {
    IMapper mapper = store.getMapperFactory().getMapper(embeddedObject.getClass());
    store.getStoreObjectFactory().createStoreObject(mapper, embeddedObject, result -> {
      if (result.failed()) {
        fail(result.cause(), handler);
      } else {
        JsonObject jo = (JsonObject) result.result().getContainer();
        success(jo, handler);
      }
    });
  }

}
