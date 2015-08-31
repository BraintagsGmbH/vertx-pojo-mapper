/*
 * Copyright 2015 Braintags GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * You may elect to redistribute this code under this licenses.
 */

package de.braintags.io.vertx.pojomapper.mapping.impl;

import java.util.HashMap;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;

/**
 * Default implementation of {@link IMapperFactory}
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
    Mapper mapper = createMapper(mapperClass);
    mappedClasses.put(className, mapper);
    return mapper;
  }

  /**
   * Creates a new instance of IMapper for the given class
   * 
   * @param mapperClass
   *          the class to be mapped
   * @return the mapper
   */
  protected Mapper createMapper(Class<?> mapperClass) {
    return new Mapper(mapperClass, this);
  }

  /**
   * Get the parent datastore, which was creating the current instance
   * 
   * @return the datastore
   */
  @Override
  public IDataStore getDataStore() {
    return dataStore;
  }
}
