/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.json.typehandler.handler;

import java.lang.annotation.Annotation;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IStoreObjectFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerResult;
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
   * de.braintags.vertx.jomnigate.typehandler.AbstractTypeHandler#matchesAnnotation(java.lang.annotation.Annotation)
   */
  @Override
  protected boolean matchesAnnotation(Annotation annotation) {
    return annotation != null && annotation instanceof Embedded;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.json.typehandler.handler.ArrayTypeHandler#fromStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object dbValue, IProperty field, Class<?> cls, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    IDataStore store = field.getMapper().getMapperFactory().getDataStore();
    Class<?> internalMapperClass = cls != null ? cls : field.getType();
    if (store.getMapperFactory().isMapper(internalMapperClass)) {
      readSingleValueAsMapper(store, internalMapperClass, dbValue, handler);
    } else {
      fail(new MappingException("Embedded should be used for mappable pojos only: " + internalMapperClass.getName()),
          handler);
    }
  }

  protected void readSingleValueAsMapper(IDataStore<JsonObject, ?> store, Class<?> internalMapperClass, Object dbValue,
      Handler<AsyncResult<ITypeHandlerResult>> handler) {
    try {
      if (dbValue == null) {
        success(null, handler);
      } else {
        IMapper<?> mapper = store.getMapperFactory().getMapper(internalMapperClass);
        JsonObject job;
        if (dbValue instanceof String) {
          job = new JsonObject((String) dbValue);
        } else if (dbValue instanceof JsonObject) {
          job = (JsonObject) dbValue;
        } else {
          fail(new UnsupportedOperationException("only String and JsonObject allowed here"), handler);
          return;
        }
        IStoreObjectFactory<JsonObject> jd = store.getStoreObjectFactory();
        jd.createStoreObject(job, mapper, result -> {
          if (result.failed()) {
            fail(result.cause(), handler);
          } else {
            Object jo = result.result().getEntity();
            success(jo, handler);
          }
        });
      }
    } catch (Exception e) {
      fail(e, handler);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.json.typehandler.handler.ArrayTypeHandler#intoStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void intoStore(Object embeddedObject, IProperty field, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    if (embeddedObject == null) {
      success(null, handler);
    } else {
      IDataStore<?, ?> store = field.getMapper().getMapperFactory().getDataStore();
      IMapper<?> embeddedMapper = store.getMapperFactory().getMapper(embeddedObject.getClass());
      if (embeddedMapper == null) {
        fail(new MappingException("Embedded should be used for mappable pojos only: " + field.getFullName()), handler);
      } else {
        writeSingleValueAsMapper(store, embeddedObject, embeddedMapper, field, handler);
      }
    }
  }

  /**
   * 
   * @param store
   *          the datastore to be used
   * @param embeddedObject
   *          the embedded object to be written
   * @param embeddedMapper
   *          the mapper, which describes the embedded object
   * @param field
   *          the field, where the embedded object is stored
   * @param handler
   *          the hander to be informed
   */
  protected void writeSingleValueAsMapper(IDataStore<?, ?> store, Object embeddedObject, IMapper embeddedMapper,
      IProperty field, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    store.getStoreObjectFactory().createStoreObject(embeddedMapper, embeddedObject, result -> {
      if (result.failed()) {
        fail(result.cause(), handler);
      } else {
        JsonObject jo = (JsonObject) result.result().getContainer();
        success(jo, handler);
      }
    });
  }

}
