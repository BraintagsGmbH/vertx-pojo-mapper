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

public class EmbeddedMapper_Array {
  @Id
  public String id;
  @Embedded
  public SimpleMapper[] simpleMapper;

  /**
   * 
   */
  public EmbeddedMapper_Array() {
    simpleMapper = new SimpleMapper[5];
    for (int i = 0; i < simpleMapper.length; i++) {
      simpleMapper[i] = new SimpleMapper("name " + i, "sec prop " + i);
    }
  }

}
