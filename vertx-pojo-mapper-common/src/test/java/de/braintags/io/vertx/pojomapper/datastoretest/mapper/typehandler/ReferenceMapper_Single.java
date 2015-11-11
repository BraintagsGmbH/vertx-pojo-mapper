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
public class ReferenceMapper_Single extends BaseRecord {

  @Referenced
  public SimpleMapper simpleMapper;

  /**
   * 
   */
  public ReferenceMapper_Single() {
    simpleMapper = new SimpleMapper("derSimpleMapper", "die prop");
  }

  @Override
  public boolean equals(Object o) {
    ReferenceMapper_Single om = (ReferenceMapper_Single) o;
    boolean equal = compareId(om.id, id);
    boolean equal2 = (om.simpleMapper == null && simpleMapper == null)
        || (om.simpleMapper != null && simpleMapper != null && simpleMapper.equals(om.simpleMapper));
    return equal && equal2;
  }

}
