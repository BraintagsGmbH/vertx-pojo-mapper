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
import de.braintags.vertx.jomnigate.mapping.IPropertyMapperFactory;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;

/**
 * Default implementation of {@link IMapperFactory}
 * 
 * @author Michael Remme
 * 
 */

public class MapperFactory extends AbstractMapperFactory {
  private final Map<String, IMapper> mappedClasses = new HashMap<>();
  private ITypeHandlerFactory typeHandlerFactory;
  private IPropertyMapperFactory propertyMapperFactory;

  /**
   * 
   */
  public MapperFactory(IDataStore<?, ?> dataStore, ITypeHandlerFactory typeHandlerFactory,
      IPropertyMapperFactory propertyMapperFactory) {
    super(dataStore);
    this.typeHandlerFactory = typeHandlerFactory;
    this.propertyMapperFactory = propertyMapperFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IMapperFactory#getMapper(java.lang.Class)
   */
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
    Mapper<T> mapper = createMapper(mapperClass);
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
  protected <T> Mapper<T> createMapper(Class<T> mapperClass) {
    return new Mapper<>(mapperClass, this);
  }

  @Override
  public final boolean isMapper(Class<?> mapperClass) {
    if (mappedClasses.containsKey(mapperClass.getName()) || mapperClass.isAnnotationPresent(Entity.class))
      return true;
    return false;
  }

  /**
   * @deprecated will be removed after complete switch to jackson
   */
  @Deprecated
  @Override
  public final ITypeHandlerFactory getTypeHandlerFactory() {
    return typeHandlerFactory;
  }

  /**
   * Set the {@link ITypeHandlerFactory} which shall be used by the current implementation
   * 
   * @param typeHandlerFactory
   *          the typeHandlerFactory to set
   * @deprecated will be removed after complete switch to jackson
   */
  @Deprecated
  protected final void setTypeHandlerFactory(ITypeHandlerFactory typeHandlerFactory) {
    this.typeHandlerFactory = typeHandlerFactory;
  }

  /**
   * @deprecated will be removed after complete switch to jackson
   * 
   */
  @Deprecated
  @Override
  public final IPropertyMapperFactory getPropertyMapperFactory() {
    return propertyMapperFactory;
  }

  /**
   * @param propertyMapperFactory
   *          the propertyMapperFactory to set
   * @deprecated will be removed after complete switch to jackson
   */
  @Deprecated
  protected final void setPropertyMapperFactory(IPropertyMapperFactory propertyMapperFactory) {
    this.propertyMapperFactory = propertyMapperFactory;
  }

}
