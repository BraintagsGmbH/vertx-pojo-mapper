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
package de.braintags.io.vertx.pojomapper.mongo;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeSave;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.MongoStoreObject;

/**
 * An implementation for Mongo
 *
 * @author Michael Remme
 * 
 */

public class MongoStoreObjectFactory implements IStoreObjectFactory {

  /**
   * 
   */
  public MongoStoreObjectFactory() {
  }

  @Override
  public void createStoreObject(IMapper mapper, Object entity, Handler<AsyncResult<IStoreObject<?>>> handler) {
    mapper.executeLifecycle(BeforeSave.class, entity);
    MongoStoreObject storeObject = new MongoStoreObject(mapper, entity);
    storeObject.initFromEntity(initResult -> {
      if (initResult.failed()) {
        handler.handle(Future.failedFuture(initResult.cause()));
      } else {
        handler.handle(Future.succeededFuture(storeObject));
      }
    });
  }

  @Override
  public void createStoreObject(Object storedObject, IMapper mapper, Handler<AsyncResult<IStoreObject<?>>> handler) {
    MongoStoreObject storeObject = new MongoStoreObject((JsonObject) storedObject, mapper);
    storeObject.initToEntity(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture(storeObject));
      }
    });
  }

}
