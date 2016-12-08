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
package de.braintags.io.vertx.pojomapper.mapping;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

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
  IDataStore getDataStore();

  /**
   * Get the propriate {@link ITypeHandlerFactory} for the current implementation
   * 
   * @return the {@link ITypeHandlerFactory}
   */
  ITypeHandlerFactory getTypeHandlerFactory();

  /**
   * Get the instance of {@link IPropertyMapperFactory} which is used by the current implementation
   * 
   * @return the {@link IPropertyMapperFactory} to retrieve new instances of {@link IPropertyMapper}
   */
  IPropertyMapperFactory getPropertyMapperFactory();

  /**
   * Get the {@link IStoreObjectFactory} suitable for the current instance
   * 
   * @return the instance of {@link IStoreObjectFactory}
   */
  public IStoreObjectFactory<?> getStoreObjectFactory();

}
