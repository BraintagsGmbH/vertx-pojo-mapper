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

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
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
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : parse((T) source), resultHandler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
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
