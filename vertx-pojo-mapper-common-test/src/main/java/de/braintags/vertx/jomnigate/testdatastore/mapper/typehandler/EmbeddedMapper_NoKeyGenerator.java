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
import de.braintags.vertx.jomnigate.testdatastore.mapper.NoKeyGeneratorMapper;

/**
 * Mapper to test {@link Embedded} annotation. This mapper should fail, cause embedded instance is no Entity
 *
 * @author Michael Remme
 * 
 */

@Entity
public class EmbeddedMapper_NoKeyGenerator extends BaseRecord {

  @Embedded
  public NoKeyGeneratorMapper nullKeyGeneratorMapper = new NoKeyGeneratorMapper();

  /**
   * 
   */
  public EmbeddedMapper_NoKeyGenerator() {
  }

}
