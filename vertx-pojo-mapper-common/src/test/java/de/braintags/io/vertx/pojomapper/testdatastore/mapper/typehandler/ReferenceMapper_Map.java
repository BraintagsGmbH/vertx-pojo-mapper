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
package de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler;

import java.util.HashMap;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.SimpleMapper;

/**
 * Mapper to test {@link Referenced} annotation
 *
 * @author Michael Remme
 * 
 */

@Entity
public class ReferenceMapper_Map extends BaseRecord {

  @Referenced
  public Map<Integer, SimpleMapper> simpleMapper;

  public ReferenceMapper_Map() {
  }

  /**
   * 
   */
  public ReferenceMapper_Map(int numberOfSubRecords) {
    simpleMapper = new HashMap<Integer, SimpleMapper>();
    for (int i = 0; i < numberOfSubRecords; i++) {
      simpleMapper.put(i, new SimpleMapper("referenceMapperMap " + i, "sec prop " + i));
    }
  }

}
