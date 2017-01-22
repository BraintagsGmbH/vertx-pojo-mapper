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
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.testdatastore.mapper.SimpleMapper;

/**
 * Mapper to test {@link Referenced} annotation
 *
 * @author Michael Remme
 * 
 */

@Entity
public class ReferenceMapper_Array extends BaseRecord {
  @Referenced
  public SimpleMapper[] simpleMapper;

  public ReferenceMapper_Array() {
  }

  /**
   * 
   */
  public ReferenceMapper_Array(int numberOfSubRecords) {
    simpleMapper = new SimpleMapper[numberOfSubRecords];
    for (int i = 0; i < simpleMapper.length; i++) {
      simpleMapper[i] = new SimpleMapper("referencedArray " + i, "sec prop " + i);
    }
  }

}
