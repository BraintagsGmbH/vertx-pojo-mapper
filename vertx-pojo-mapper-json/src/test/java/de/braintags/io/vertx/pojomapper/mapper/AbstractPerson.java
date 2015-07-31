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
