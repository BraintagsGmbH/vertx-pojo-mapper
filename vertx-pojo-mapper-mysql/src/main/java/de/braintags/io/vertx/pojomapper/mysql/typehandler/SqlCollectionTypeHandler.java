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
package de.braintags.io.vertx.pojomapper.mysql.typehandler;

import de.braintags.io.vertx.pojomapper.json.typehandler.handler.CollectionTypeHandler;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * Extension of {@link CollectionTypeHandler} which is using the generated JsonArray to save into the database
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
   * @see de.braintags.io.vertx.pojomapper.json.typehandler.handler.CollectionTypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    JsonArray array = new JsonArray((String) source);
    super.fromStore(array, field, cls, resultHandler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.json.typehandler.handler.CollectionTypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    super.intoStore(source, field, result -> {
      if (result.failed()) {
        resultHandler.handle(result);
        return;
      }
      JsonArray array = (JsonArray) result.result().getResult();
      String res = array.encode();
      success(res, resultHandler);
    });
  }

}
