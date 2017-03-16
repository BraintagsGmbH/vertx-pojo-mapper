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

import de.braintags.vertx.jomnigate.mapping.IProperty;
import de.braintags.vertx.jomnigate.typehandler.AbstractTypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * Abstract implementation of {@link ITypeHandler} which encodes entities into {@link JsonObject}
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractJsonTypeHandler<T, V> extends AbstractTypeHandler {

  /**
   * @param typeHandlerFactory
   * @param classesToDeal
   */
  public AbstractJsonTypeHandler(ITypeHandlerFactory typeHandlerFactory, Class<?>... classesToDeal) {
    super(typeHandlerFactory, classesToDeal);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object source, IProperty field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : parse((T) source), resultHandler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void intoStore(Object source, IProperty field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : encode((V) source), resultHandler);
  }

  /**
   * Encodes the instance into a JsonObject to be stored inside the datastore
   * 
   * @param source
   * @return the encoded instance
   */
  protected abstract T encode(V source);

  /**
   * Parses the instance coming out of the datastore
   * 
   * @param source
   * @return the parsed instance
   */
  protected abstract V parse(T source);

}
