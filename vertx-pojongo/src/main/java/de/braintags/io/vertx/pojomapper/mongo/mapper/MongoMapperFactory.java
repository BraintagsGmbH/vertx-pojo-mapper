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

package de.braintags.io.vertx.pojomapper.mongo.mapper;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.mapping.impl.Mapper;
import de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory;

/**
 * An extension of {@link MapperFactory}
 *
 * @author Michael Remme
 * 
 */

public class MongoMapperFactory extends MapperFactory {

  /**
   * @param dataStore
   */
  public MongoMapperFactory(IDataStore dataStore) {
    super(dataStore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory#createMapper(java.lang.Class)
   */
  @Override
  protected Mapper createMapper(Class mapperClass) {
    return new MongoMapper(mapperClass, this);
  }

}
