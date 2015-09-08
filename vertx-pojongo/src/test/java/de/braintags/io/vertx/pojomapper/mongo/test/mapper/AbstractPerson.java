/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mongo.test.mapper;

import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeLoad;
import de.braintags.io.vertx.pojomapper.test.mapper.IPerson;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractPerson implements IPerson {

  @BeforeLoad
  public void handleBeforeLoad() {
    System.out.println("handleBeforeLoad");
  }

}
