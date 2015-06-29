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

import java.util.HashMap;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class MapperFactory implements IMapperFactory {
  private IDataStore dataStore;
  private final Map<String, IMapper> mappedClasses = new HashMap<String, IMapper>();

  /**
   * 
   */
  public MapperFactory(IDataStore dataStore) {
    this.dataStore = dataStore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IMapperFactory#getMapper(java.lang.Class)
   */
  @Override
  public IMapper getMapper(Class<?> mapperClass) {
    String className = mapperClass.getName();
    if (mappedClasses.containsKey(className))
      return mappedClasses.get(className);
    Mapper mapper = new Mapper(mapperClass, this);
    mappedClasses.put(className, mapper);
    return mapper;
  }

  /**
   * Get the parent datastore, which was creating the current instance
   * 
   * @return the datastore
   */
  IDataStore getDataStore() {
    return dataStore;
  }
}
