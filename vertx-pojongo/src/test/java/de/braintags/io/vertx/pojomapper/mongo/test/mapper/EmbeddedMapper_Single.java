/*
 *
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mongo.test.mapper;

import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;

/**
 * Mapper to test {@link Referenced} annotation
 *
 * @author Michael Remme
 * 
 */

public class EmbeddedMapper_Single {
  @Id
  public String id;
  @Embedded
  public SimpleMapper simpleMapper;

  /**
   * 
   */
  public EmbeddedMapper_Single() {
  }

  @Override
  public boolean equals(Object o) {
    EmbeddedMapper_Single om = (EmbeddedMapper_Single) o;
    boolean equal = om.id.equals(id);
    boolean equal2 = om.simpleMapper.equals(simpleMapper);
    return equal && equal2;
  }

}
