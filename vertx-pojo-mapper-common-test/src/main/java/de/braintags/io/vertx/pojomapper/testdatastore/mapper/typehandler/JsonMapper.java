/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import io.vertx.core.json.JsonObject;

/**
 * Mapper with a JsonObject
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class JsonMapper extends BaseRecord {
  public JsonObject json;

  /**
   * 
   */
  public JsonMapper() {
    json = new JsonObject();
    json.put("testkey", 50);
    json.put("testkey2", "my text");
  }

}
