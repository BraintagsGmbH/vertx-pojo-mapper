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
