/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mapper;

import de.braintags.io.vertx.pojomapper.annotation.ObjectFactory;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeLoad;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

@ObjectFactory(className = "de.braintags.io.vertx.pojomapper.impl.DummyObjectFactory")
public abstract class AbstractPerson implements IPerson {

  @BeforeLoad
  public void handleBeforeLoad() {
    System.out.println("handleBeforeLoad");
  }

}
