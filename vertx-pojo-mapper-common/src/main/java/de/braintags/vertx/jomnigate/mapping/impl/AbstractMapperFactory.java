/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mapping.impl;

import java.util.HashMap;
import java.util.Map;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;

/**
 * An abstract implementation of IMapperFactory
 * 
 * @author Michael Remme
 */
public abstract class AbstractMapperFactory implements IMapperFactory {
  private IDataStore<?, ?> datastore;
  private final Map<String, IMapper> mappedClasses = new HashMap<>();

  /**
   * @param dataStore
   */
  public AbstractMapperFactory(IDataStore<?, ?> dataStore) {
    this.datastore = dataStore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapperFactory#getDataStore()
   */
  @Override
  public final IDataStore<?, ?> getDataStore() {
    return datastore;
  }

  @Override
  public final <T> IMapper<T> getMapper(Class<T> mapperClass) {
    String className = mapperClass.getName();
    if (mappedClasses.containsKey(className)) {
      @SuppressWarnings("unchecked")
      IMapper<T> cachedEntry = mappedClasses.get(className);
      return cachedEntry;
    }
    if (!mapperClass.isAnnotationPresent(Entity.class))
      throw new UnsupportedOperationException(String
          .format("The class %s is no mappable entity. Add the annotation Entity to the class", mapperClass.getName()));
    IMapper<T> mapper = createMapper(mapperClass);
    mappedClasses.put(className, mapper);
    return mapper;
  }

  @Override
  public final boolean isMapper(Class<?> mapperClass) {
    if (mappedClasses.containsKey(mapperClass.getName()) || mapperClass.isAnnotationPresent(Entity.class))
      return true;
    return false;
  }

  /**
   * Creates a new instance of IMapper for the given class
   * 
   * @param mapperClass
   *          the class to be mapped
   * @return the mapper
   */
  protected abstract <T> IMapper<T> createMapper(Class<T> mapperClass);

}
