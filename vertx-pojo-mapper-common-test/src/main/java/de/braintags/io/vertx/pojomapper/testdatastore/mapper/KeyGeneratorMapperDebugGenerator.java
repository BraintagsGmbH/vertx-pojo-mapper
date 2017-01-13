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
package de.braintags.io.vertx.pojomapper.testdatastore.mapper;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.KeyGenerator;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.mapping.impl.keygen.DebugGenerator;

/**
 * A simple mapper with some beans properties
 *
 * @author Michael Remme
 * 
 */

@Entity
@KeyGenerator(value = DebugGenerator.NAME)
public class KeyGeneratorMapperDebugGenerator {
  @Id
  public String id;
  public String name;

}
