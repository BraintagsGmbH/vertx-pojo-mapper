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
package de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.SimpleMapper;

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

  /**
   * 
   */
  public ReferenceMapper_Array() {
    simpleMapper = new SimpleMapper[5];
    for (int i = 0; i < simpleMapper.length; i++) {
      simpleMapper[i] = new SimpleMapper("name " + i, "sec prop " + i);
    }
  }

}
