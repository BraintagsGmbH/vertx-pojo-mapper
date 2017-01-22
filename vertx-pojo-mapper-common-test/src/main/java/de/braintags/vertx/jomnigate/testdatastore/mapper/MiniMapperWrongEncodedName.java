/*-
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
import de.braintags.vertx.jomnigate.annotation.field.Encoder;
import de.braintags.vertx.jomnigate.annotation.field.Id;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

@Entity
public class MiniMapperWrongEncodedName {
  @Id
  public String id = null;
  public String name = "testName";
  @Encoder(name = "NotExists")
  public Object myObjectClass;

  public transient String transientString;

  public MiniMapperWrongEncodedName() {
  }

  public MiniMapperWrongEncodedName(String name) {
    this.name = name;
  }

}
