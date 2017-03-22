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

/**
 * Mapper to test {@link Embedded} annotation. This mapper should fail, cause embedded instance is no Entity
 *
 * @author Michael Remme
 * 
 */

@Entity
public class EmbeddedMapper_Single_Failure extends BaseRecord {

  @Embedded
  public Boolean simpleMapper;

  /**
   * 
   */
  public EmbeddedMapper_Single_Failure() {
  }

  @Override
  public boolean equals(Object o) {
    EmbeddedMapper_Single_Failure om = (EmbeddedMapper_Single_Failure) o;
    boolean equal = om.id.equals(id);
    boolean equal2 = (om.simpleMapper == null && simpleMapper == null)
        || (om.simpleMapper != null && simpleMapper != null && om.simpleMapper.equals(simpleMapper));
    return equal && equal2;
  }

}
