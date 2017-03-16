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
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;
import de.braintags.vertx.jomnigate.mapping.IObjectReference;
import de.braintags.vertx.jomnigate.mapping.impl.ObjectReference;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerReferenced;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * handles Collections which are annotated to be {@link Referenced}
 * 
 * @author Michael Remme
 * 
 */

public class CollectionTypeHandlerReferenced extends CollectionTypeHandler implements ITypeHandlerReferenced {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public CollectionTypeHandlerReferenced(ITypeHandlerFactory typeHandlerFactory) {
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
    return annotation != null && annotation instanceof Referenced;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object source, IProperty field, Class<?> cls, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    Class<?> mapperClass = cls != null ? cls : field.getType();
    if (mapperClass == null) {
      fail(new NullPointerException("undefined mapper class"), handler);
      return;
    }
    if (field.getMapper().handleReferencedRecursive()) {
      IDataStore store = field.getMapper().getMapperFactory().getDataStore();
      ObjectReference objectReference = new ObjectReference(field, source);
      resolveReferencedObject(store, objectReference, handler);
    } else {
      ObjectReference objectReference = new ObjectReference(field, source);
      success(objectReference, handler);
    }
  }

  @Override
  public void resolveReferencedObject(IDataStore store, IObjectReference reference,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    super.fromStore(reference.getDbSource(), reference.getField(), null, resultHandler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.json.typehandler.handler.CollectionTypeHandler#handleObjectFromStore(de.braintags.
   * vertx.jomnigate.mapping.IField, de.braintags.vertx.jomnigate.typehandler.ITypeHandler, java.lang.Object)
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  protected Future handleObjectFromStore(IProperty field, ITypeHandler subHandler, Object o) {
    if (subHandler instanceof ObjectTypeHandlerReferenced) {
      Future f = Future.future();
      IDataStore store = field.getMapper().getMapperFactory().getDataStore();
      IMapperFactory mf = store.getMapperFactory();
      IMapper subMapper = mf.getMapper(field.getSubClass());
      ((ObjectTypeHandlerReferenced) subHandler).getReferencedObjectById(store, subMapper, o, thr -> {
        if (thr.failed()) {
          f.fail(thr.cause());
        } else {
          f.complete(thr.result().getResult());
        }
      });
      return f;
    } else {
      return Future.failedFuture(new UnsupportedOperationException("Need a ObjectTypeHandlerReferenced here! "));
    }
  }

}
