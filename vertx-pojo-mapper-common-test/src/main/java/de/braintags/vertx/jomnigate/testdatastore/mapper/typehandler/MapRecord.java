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

import java.util.HashMap;
import java.util.Map;

import de.braintags.vertx.jomnigate.annotation.Entity;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class MapRecord extends BaseRecord {
  private Map<String, String> map = new HashMap<>();

  public MapRecord() {
    map.put("Eins", "1");
    map.put("Zwei", "2");
    map.put("Drei", "3");

    // map2.put("tk", new CollectionRecord());
  }

  /**
   * @return the map
   */
  public Map<String, String> getMap() {
    return map;
  }

  /**
   * @param map
   *          the map to set
   */
  public void setMap(Map<String, String> map) {
    this.map = map;
  }

}
