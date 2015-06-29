/*
 * Copyright 2014 Red Hat, Inc.
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

package de.braintags.io.vertx.pojomapper.mapping.impl;

import de.braintags.io.vertx.pojomapper.mapping.IObjectFactory;

/**
 * Default implementation of {@link IObjectFactory}
 * 
 * @author Michael Remme
 * 
 */

public class ObjectFactory implements IObjectFactory {

  /**
   * 
   */
  public ObjectFactory() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IObjectFactory#createInstance(java.lang.Class)
   */
  @Override
  public <T> T createInstance(Class<T> clazz) {
    throw new UnsupportedOperationException();
  }

}
