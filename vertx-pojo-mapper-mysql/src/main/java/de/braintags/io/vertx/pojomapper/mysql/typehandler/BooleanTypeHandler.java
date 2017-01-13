/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mysql.typehandler;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * 
 * @author Michael Remme
 * 
 */

public class BooleanTypeHandler extends AbstractTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public BooleanTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, boolean.class, Boolean.class);
  }

  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? null : ((byte) source == 1) ? true : false, resultHandler);
  }

  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : ((Boolean) source).booleanValue() == Boolean.TRUE ? 1 : 0, resultHandler);
  }

}
