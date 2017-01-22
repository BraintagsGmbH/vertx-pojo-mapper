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
package de.braintags.vertx.jomnigate.mysql.typehandler;

import de.braintags.vertx.jomnigate.json.typehandler.handler.CollectionTypeHandler;
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * 
 * @author Michael Remme
 * 
 */

public class SqlCollectionTypeHandler extends CollectionTypeHandler {

  /**
   * @param typeHandlerFactory
   */
  public SqlCollectionTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.json.typehandler.handler.ArrayTypeHandler#fromStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    try {
      JsonArray sourceArray = source == null ? null : new JsonArray((String) source);
      super.fromStore(sourceArray, field, cls, handler);
    } catch (Exception e) {
      fail(e, handler);
    }
  }

  @Override
  protected Object encodeResultArray(JsonArray result) {
    return result.encode();
  }

}
