/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.typehandler.stringbased.handlers;

import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.typehandler.AbstractTypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * implementation of {@link ITypeHandler} for {@link JsonObject}
 * 
 * @author Michael Remme
 * 
 */

public class JsonTypeHandler extends AbstractTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public JsonTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, JsonObject.class);
  }

  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : new JsonObject((String) source), resultHandler);
  }

  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : ((JsonObject) source).toString(), resultHandler);
  }

}
