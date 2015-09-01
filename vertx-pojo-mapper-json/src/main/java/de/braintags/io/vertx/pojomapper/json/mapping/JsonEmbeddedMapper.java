/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.json.mapping;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.mapping.IEmbeddedMapper;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;

/**
 * Implementation of {@link IEmbeddedMapper} which is used to store subobjects embedded in the field of their parent
 * instance
 * 
 * @author Michael Remme
 * 
 */

public class JsonEmbeddedMapper extends AbstractSubobjectMapper implements IEmbeddedMapper {

  /**
   * 
   */
  public JsonEmbeddedMapper() {
  }

  @Override
  public void writeSingleValue(Object embeddedObject, IStoreObject<?> storeObject, IField field,
      Handler<AsyncResult<Object>> handler) {
    IDataStore store = field.getMapper().getMapperFactory().getDataStore();
    IMapper mapper = store.getMapperFactory().getMapper(embeddedObject.getClass());

    store.getStoreObjectFactory().createStoreObject(mapper, embeddedObject, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        JsonObject jo = (JsonObject) result.result().getContainer();
        handler.handle(Future.succeededFuture(jo));
      }
    });

  }

  @Override
  public void readSingleValue(Object dbValue, IField field, Class<?> mapperClass, Handler<AsyncResult<Object>> handler) {
    IDataStore store = field.getMapper().getMapperFactory().getDataStore();
    mapperClass = mapperClass != null ? mapperClass : field.getType();
    IMapper mapper = store.getMapperFactory().getMapper(mapperClass);

    store.getStoreObjectFactory().createStoreObject(dbValue, mapper, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        Object jo = result.result().getEntity();
        handler.handle(Future.succeededFuture(jo));
      }
    });
  }

}
