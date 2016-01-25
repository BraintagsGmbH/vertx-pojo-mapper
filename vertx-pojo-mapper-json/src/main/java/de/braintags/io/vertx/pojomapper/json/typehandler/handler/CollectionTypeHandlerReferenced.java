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

import java.lang.annotation.Annotation;
import java.util.Collection;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IObjectReference;
import de.braintags.io.vertx.pojomapper.mapping.impl.ObjectReference;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerReferenced;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
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
   * de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler#matchesAnnotation(java.lang.annotation.Annotation)
   */
  @Override
  protected boolean matchesAnnotation(Annotation annotation) {
    return annotation != null && annotation instanceof Referenced;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls, Handler<AsyncResult<ITypeHandlerResult>> handler) {
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
   * de.braintags.io.vertx.pojomapper.json.typehandler.handler.CollectionTypeHandler#handleObjectFromStore(java.lang.
   * Object, de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler, java.util.Collection,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  protected void handleObjectFromStore(Object o, ITypeHandler subHandler, Collection coll, IField field,
      Handler<AsyncResult<Void>> resultHandler) {
    if (subHandler instanceof ObjectTypeHandlerReferenced) {
      IDataStore store = field.getMapper().getMapperFactory().getDataStore();
      IMapperFactory mf = store.getMapperFactory();
      IMapper subMapper = mf.getMapper(field.getSubClass());
      ((ObjectTypeHandlerReferenced) subHandler).getReferencedObjectById(store, subMapper, o, tmpResult -> {
        if (tmpResult.failed()) {
          resultHandler.handle(Future.failedFuture(tmpResult.cause()));
          return;
        }
        Object dest = tmpResult.result().getResult();
        coll.add(dest);
        resultHandler.handle(Future.succeededFuture());
      });
    } else {
      resultHandler
          .handle(Future.failedFuture(new UnsupportedOperationException("Need a ObjectTypeHandlerReferenced here! ")));

    }
  }

}
