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

import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * handles Collections which are annotated to be {@link Embedded}
 * 
 * @author Michael Remme
 * 
 */

public class MapTypeHandlerEmbedded extends MapTypeHandler {
  private ITypeHandler th = new ObjectTypeHandlerEmbedded(getTypeHandlerFactory());

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public MapTypeHandlerEmbedded(ITypeHandlerFactory typeHandlerFactory) {
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
   * @see de.braintags.vertx.jomnigate.json.typehandler.handler.MapTypeHandler#getValueTypeHandler(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField)
   */
  @Override
  protected ITypeHandler getValueTypeHandler(Object value, IProperty field) {
    return th;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.json.typehandler.handler.MapTypeHandler#convertValueFromStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  protected void convertValueFromStore(Object valueIn, IProperty field, Handler<AsyncResult<Object>> resultHandler) {
    th.fromStore(valueIn, field, field.getSubClass(), valueResult -> {
      if (valueResult.failed()) {
        resultHandler.handle(Future.failedFuture(valueResult.cause()));
      } else {
        Object javaValue = valueResult.result().getResult();
        resultHandler.handle(Future.succeededFuture(javaValue));
      }
    });
  }

}
