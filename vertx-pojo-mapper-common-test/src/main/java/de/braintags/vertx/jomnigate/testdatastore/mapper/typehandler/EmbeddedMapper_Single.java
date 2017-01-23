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
import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;

/**
 * Mapper to test {@link Referenced} annotation
 *
 * @author Michael Remme
 * 
 */

@Entity
public class EmbeddedMapper_Single extends BaseRecord {

  @Embedded
  public SimpleMapperEmbedded simpleMapper;

  /**
   * 
   */
  public EmbeddedMapper_Single() {
  }

  @Override
  public boolean equals(Object o) {
    EmbeddedMapper_Single om = (EmbeddedMapper_Single) o;
    boolean equal = om.id.equals(id);
    boolean equal2 = (om.simpleMapper == null && simpleMapper == null)
        || (om.simpleMapper != null && simpleMapper != null && om.simpleMapper.equals(simpleMapper));
    return equal && equal2;
  }

}
