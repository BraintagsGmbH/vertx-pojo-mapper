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
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.typehandler.AbstractTypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Deals all fields, which are instanced of Object and which are NOT annotated as {@link Referenced} or {@link Embedded}
 * 
 * @author Michael Remme
 * 
 */
public class ObjectTypeHandler extends AbstractTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public ObjectTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Object.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.typehandler.AbstractTypeHandler#matchesAnnotation(java.lang.annotation.Annotation)
   */
  @Override
  protected boolean matchesAnnotation(Annotation annotation) {
    return annotation == null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.typehandler.ITypeHandler#fromStore(java.lang.Object)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source, resultHandler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.typehandler.ITypeHandler#intoStore(java.lang.Object)
   */
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source, resultHandler);
  }

}
