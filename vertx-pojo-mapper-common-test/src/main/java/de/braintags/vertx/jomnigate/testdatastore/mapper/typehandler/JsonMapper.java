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
package de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler;

import de.braintags.vertx.jomnigate.annotation.Entity;
import io.vertx.core.json.JsonObject;

/**
 * Mapper with a JsonObject
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class JsonMapper extends BaseRecord {
  private JsonObject json;

  /**
   * 
   */
  public JsonMapper() {
    json = new JsonObject();
    json.put("testkey", 50);
    json.put("testkey2", "my text");
  }

  /**
   * @return the json
   */
  public JsonObject getJson() {
    return json;
  }

  /**
   * @param json
   *          the json to set
   */
  public void setJson(JsonObject json) {
    this.json = json;
  }

}
