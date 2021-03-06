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
package de.braintags.vertx.jomnigate.mapping;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;

/**
 * IMapperFactory is responsible to create and store instances of {@link IMapper} for all classes, which shall be
 * persisted into the datastore
 * 
 * @author Michael Remme
 * 
 */
public interface IMapperFactory {

  /**
   * Retrieve the {@link IMapper} for the given class
   * 
   * @param mapperClass
   * @return
   * @throws Exception
   *           any Exception which can occur in the init process
   */
  <T> IMapper<T> getMapper(Class<T> mapperClass);

  /**
   * Returns true, if the given class specifies a mappable class. At a minimum whic method will have to check, wether
   * the class is marked with the {@link Entity} annotation
   * 
   * @param mapperClass
   *          the class to be checkd
   * @return true, if class specifies a mapper, false otherwise
   */
  boolean isMapper(Class<?> mapperClass);

  /**
   * Get the {@link IDataStore} which created the current instance
   * 
   * @return
   */
  IDataStore<?, ?> getDataStore();

  /**
   * Reset all mapping information. Mappings will be recreated with the next request to {@link #getMapper(Class)}
   */
  void reset();

  /**
   * Get the propriate {@link ITypeHandlerFactory} for the current implementation
   * 
   * @return the {@link ITypeHandlerFactory}
   * @deprecated will be removed after complete switch to jackson
   */
  @Deprecated
  ITypeHandlerFactory getTypeHandlerFactory();

  /**
   * Get the instance of {@link IPropertyMapperFactory} which is used by the current implementation
   * 
   * @return the {@link IPropertyMapperFactory} to retrieve new instances of {@link IPropertyMapper}
   * @deprecated will be removed after complete switch to jackson
   */
  @Deprecated
  IPropertyMapperFactory getPropertyMapperFactory();

}
