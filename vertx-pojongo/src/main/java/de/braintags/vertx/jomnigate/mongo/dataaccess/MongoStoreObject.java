/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mongo.dataaccess;

import de.braintags.vertx.jomnigate.json.dataaccess.JsonStoreObject;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import io.vertx.core.json.JsonObject;

/**
 * 
 * @author Michael Remme
 */

public class MongoStoreObject<T> extends JsonStoreObject<T> {

  /**
   * Creates a new instance, where the internal container is filled from the contents of the given entity
   * 
   * @param mapper
   *          the mapper to be used
   * @param entity
   *          the entity
   */
  public MongoStoreObject(IMapper<T> mapper, T entity) {
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
  public MongoStoreObject(JsonObject json, IMapper<T> mapper) {
    super(json, mapper);
  }

}
