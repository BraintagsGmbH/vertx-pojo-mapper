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
package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import de.braintags.io.vertx.pojomapper.json.dataaccess.JsonStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.impl.DefaultPropertyMapper;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * 
 * @author Michael Remme
 */

public class MongoStoreObject extends JsonStoreObject {
  protected Object generatedId = null;

  /**
   * Creates a new instance, where the internal container is filled from the contents of the given entity
   * 
   * @param mapper
   *          the mapper to be used
   * @param entity
   *          the entity
   */
  public MongoStoreObject(IMapper mapper, Object entity) {
    super(mapper, entity);
  }

  /**
   * Creates a new instance, where the internal container is filled from the contents of the given entity
   * 
   * @param json
   *          the json object coming from the datastore
   * @param mapper
   *          the mapper to be used
   */
  public MongoStoreObject(JsonObject json, IMapper mapper) {
    super(json, mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.json.dataaccess.JsonStoreObject#initFromEntity(io.vertx.core.Handler)
   */
  @Override
  public void initFromEntity(Handler<AsyncResult<Void>> handler) {
    super.initFromEntity(res -> {
      if (res.failed()) {
        handler.handle(res);
      } else {
        if (isNewInstance() && getMapper().getKeyGenerator() != null) {
          getNextId(handler);
        } else {
          handler.handle(res);
        }
      }
    });
  }

  /**
   * IN case of a defined {@link IKeyGenerator} the next id is requested for a new record
   * 
   * @param handler
   */
  public void getNextId(Handler<AsyncResult<Void>> handler) {
    IKeyGenerator gen = getMapper().getKeyGenerator();
    gen.generateKey(getMapper(), keyResult -> {
      if (keyResult.failed()) {
        handler.handle(Future.failedFuture(keyResult.cause()));
      } else {
        generatedId = keyResult.result().getKey();
        IField field = getMapper().getIdField();
        ITypeHandler th = field.getTypeHandler();
        DefaultPropertyMapper.intoStoreObject(this, field, th, generatedId, result -> {
          if (result.failed()) {
            handler.handle(result);
          } else {
            setNewInstance(true);
            handler.handle(result);
          }
        });
      }
    });
  }

}
