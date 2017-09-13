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

package de.braintags.vertx.jomnigate.mysql.dataaccess;

import java.util.Map;

import de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeSave;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IStoreObject;
import de.braintags.vertx.jomnigate.mapping.impl.AbstractStoreObjectFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * 
 * @author Michael Remme
 * 
 */

public class SqlStoreObjectFactory extends AbstractStoreObjectFactory<Object> {

  /**
   * Craetes a new instance of IStoreObject by using a {@link Map} as internal format
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IStoreObjectFactory#createStoreObject(de.braintags.vertx.jomnigate.mapping.IMapper,
   *      java.lang.Object, io.vertx.core.Handler)
   */
  @Override
  public <T> void createStoreObject(final IMapper<T> mapper, final T entity,
      final Handler<AsyncResult<IStoreObject<T, Object>>> handler) {
    mapper.executeLifecycle(BeforeSave.class, entity, lcr -> {
      if (lcr.failed()) {
        handler.handle(Future.failedFuture(lcr.cause()));
      } else {
        SqlStoreObject<T> storeObject = new SqlStoreObject<>(mapper, entity);
        storeObject.initFromEntity(initResult -> {
          if (initResult.failed()) {
            handler.handle(Future.failedFuture(initResult.cause()));
          } else {
            handler.handle(Future.succeededFuture(storeObject));
          }
        });
      }
    });
  }

  @Override
  public <T> void createStoreObject(final Object storedObject, final IMapper<T> mapper,
      final Handler<AsyncResult<IStoreObject<T, Object>>> handler) {
    SqlStoreObject<T> storeObject = new SqlStoreObject<>((JsonObject) storedObject, mapper);
    storeObject.initToEntity(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture(storeObject));
      }
    });
  }

}
