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

import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
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
   * de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler#matchesAnnotation(java.lang.annotation.Annotation)
   */
  @Override
  protected boolean matchesAnnotation(Annotation annotation) {
    return annotation != null && annotation instanceof Embedded;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.json.typehandler.handler.MapTypeHandler#getValueTypeHandler(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  protected ITypeHandler getValueTypeHandler(Object value, IField field) {
    return th;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.json.typehandler.handler.MapTypeHandler#convertValueFromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  protected void convertValueFromStore(Object valueIn, IField field, Handler<AsyncResult<Object>> resultHandler) {
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
