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

package de.braintags.io.vertx.pojomapper.mysql.dataaccess;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeSave;
import de.braintags.io.vertx.pojomapper.json.dataaccess.JsonStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory;

/**
 * 
 * @author Michael Remme
 * 
 */

public class SqlStoreObjectFactory implements IStoreObjectFactory {

  /**
   * 
   */
  public SqlStoreObjectFactory() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory#createStoreObject(de.braintags.io.vertx.pojomapper
   * .mapping.IMapper, java.lang.Object, io.vertx.core.Handler)
   */
  @Override
  public void createStoreObject(IMapper mapper, Object entity, Handler<AsyncResult<IStoreObject<?>>> handler) {
    mapper.executeLifecycle(BeforeSave.class, entity);
    JsonStoreObject storeObject = new JsonStoreObject(mapper, entity);
    storeObject.initFromEntity(initResult -> {
      if (initResult.failed()) {
        handler.handle(Future.failedFuture(initResult.cause()));
      } else {
        handler.handle(Future.succeededFuture(storeObject));
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory#createStoreObject(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IMapper, io.vertx.core.Handler)
   */
  @Override
  public void createStoreObject(Object storedObject, IMapper mapper, Handler<AsyncResult<IStoreObject<?>>> handler) {
    JsonStoreObject storeObject = new JsonStoreObject((JsonObject) storedObject, mapper);
    storeObject.initToEntity(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture(storeObject));
      }
    });
  }

}
