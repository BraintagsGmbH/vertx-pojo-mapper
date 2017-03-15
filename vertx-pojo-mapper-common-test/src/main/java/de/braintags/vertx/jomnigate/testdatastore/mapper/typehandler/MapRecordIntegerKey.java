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
public class MapRecordIntegerKey extends BaseRecord {
  public Map<Integer, String> map = new HashMap<>();

  public MapRecordIntegerKey() {
    map.put(1, "1");
    map.put(2, "2");
    map.put(3, "3");

    // map2.put("tk", new CollectionRecord());
  }

}
