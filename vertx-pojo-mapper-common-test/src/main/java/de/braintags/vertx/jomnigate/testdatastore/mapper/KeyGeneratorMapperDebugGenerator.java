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
package de.braintags.vertx.jomnigate.testdatastore.mapper;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.KeyGenerator;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.mapping.impl.keygen.DebugGenerator;

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
