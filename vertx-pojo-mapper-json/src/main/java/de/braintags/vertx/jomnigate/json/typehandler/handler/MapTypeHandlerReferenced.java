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
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;
import de.braintags.vertx.jomnigate.mapping.IObjectReference;
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

public class MapTypeHandlerReferenced extends MapTypeHandler implements ITypeHandlerReferenced {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public MapTypeHandlerReferenced(ITypeHandlerFactory typeHandlerFactory) {
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

  @Override
  public void resolveReferencedObject(IDataStore store, IObjectReference reference,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    super.fromStore(reference.getDbSource(), reference.getField(), null, resultHandler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.json.typehandler.handler.MapTypeHandler#convertValueFromStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  protected void convertValueFromStore(Object valueIn, IField field, Handler<AsyncResult<Object>> resultHandler) {
    ITypeHandler subHandler = field.getSubTypeHandler();
    if (subHandler instanceof ObjectTypeHandlerReferenced) {
      IDataStore store = field.getMapper().getMapperFactory().getDataStore();
      IMapperFactory mf = store.getMapperFactory();
      IMapper subMapper = mf.getMapper(field.getSubClass());
      ((ObjectTypeHandlerReferenced) subHandler).getReferencedObjectById(store, subMapper, valueIn, tmpResult -> {
        if (tmpResult.failed()) {
          resultHandler.handle(Future.failedFuture(tmpResult.cause()));
          return;
        }
        Object javaValue = tmpResult.result().getResult();
        resultHandler.handle(Future.succeededFuture(javaValue));
      });
    } else {
      resultHandler
          .handle(Future.failedFuture(new UnsupportedOperationException("Need a ObjectTypeHandlerReferenced here! ")));
    }
  }

}
