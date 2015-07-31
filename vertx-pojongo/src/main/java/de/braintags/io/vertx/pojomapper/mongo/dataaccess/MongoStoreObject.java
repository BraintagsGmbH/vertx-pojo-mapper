/*
 *
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import io.vertx.core.json.JsonObject;
import de.braintags.io.vertx.pojomapper.json.dataaccess.JsonStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;

/**
 * 
 * @author Michael Remme
 */

public class MongoStoreObject extends JsonStoreObject {

  /**
   * Creates a new instance, where the internal container is filled from the contents of the given entity
   */
  public MongoStoreObject(IMapper mapper, Object entity) {
    super(mapper, entity);
  }

  /**
   * Creates a new instance, where the internal container is filled from the contents of the given entity
   */
  public MongoStoreObject(JsonObject json, IMapper mapper) {
    super(json, mapper);
  }

}
