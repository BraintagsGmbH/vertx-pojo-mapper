/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mapping.impl;

import java.util.HashMap;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

/**
 * Default implementation of {@link IMapperFactory}
 * 
 * @author Michael Remme
 * 
 */

public class MapperFactory implements IMapperFactory {
  private IDataStore dataStore;
  private final Map<String, IMapper> mappedClasses = new HashMap<String, IMapper>();
  private ITypeHandlerFactory typeHandlerFactory;
  private IPropertyMapperFactory propertyMapperFactory;
  private IStoreObjectFactory storeObjectFactory;

  /**
   * 
   * @param dataStore
   *          the {@link IDataStore} to be used
   */
  public MapperFactory(IDataStore dataStore, ITypeHandlerFactory typeHandlerFactory,
      IPropertyMapperFactory propertyMapperFactory, IStoreObjectFactory stf) {
    this.dataStore = dataStore;
    this.typeHandlerFactory = typeHandlerFactory;
    this.propertyMapperFactory = propertyMapperFactory;
    this.storeObjectFactory = stf;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.pojomapper.mapping.IMapperFactory#getMapper(java.lang.Class)
   */
  @Override
  public final IMapper getMapper(Class<?> mapperClass) {
    String className = mapperClass.getName();
    if (mappedClasses.containsKey(className))
      return mappedClasses.get(className);
    if (!mapperClass.isAnnotationPresent(Entity.class))
      throw new UnsupportedOperationException(String
          .format("The class %s is no mappable entity. Add the annotation Entity to the class", mapperClass.getName()));
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

  @Override
  public final boolean isMapper(Class<?> mapperClass) {
    if (mappedClasses.containsKey(mapperClass.getName()) || mapperClass.isAnnotationPresent(Entity.class))
      return true;
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.pojomapper.mapping.IMapperFactory#getTypeHandlerFactory()
   */
  @Override
  public final ITypeHandlerFactory getTypeHandlerFactory() {
    return typeHandlerFactory;
  }

  /**
   * Set the {@link ITypeHandlerFactory} which shall be used by the current implementation
   * 
   * @param typeHandlerFactory
   *          the typeHandlerFactory to set
   */
  protected final void setTypeHandlerFactory(ITypeHandlerFactory typeHandlerFactory) {
    this.typeHandlerFactory = typeHandlerFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.pojomapper.IDataStore#getPropertyMapperFactory()
   */
  @Override
  public final IPropertyMapperFactory getPropertyMapperFactory() {
    return propertyMapperFactory;
  }

  /**
   * @param propertyMapperFactory
   *          the propertyMapperFactory to set
   */
  protected final void setPropertyMapperFactory(IPropertyMapperFactory propertyMapperFactory) {
    this.propertyMapperFactory = propertyMapperFactory;
  }

  @Override
  public final IStoreObjectFactory getStoreObjectFactory() {
    return storeObjectFactory;
  }

  /**
   * @param storeObjectFactory
   *          the storeObjectFactory to set
   */
  protected final void setStoreObjectFactory(IStoreObjectFactory storeObjectFactory) {
    this.storeObjectFactory = storeObjectFactory;
  }

}
