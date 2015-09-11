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
package de.braintags.io.vertx.pojomapper;

import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapper;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableGenerator;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

/**
 * IDataStore contains information about the destination datastore and creates the handler objects
 * 
 * @author Michael Remme
 * 
 */

public interface IDataStore {

  /**
   * Returns a new {@link IQuery} bound to the given mapper
   * 
   * @param mapper
   *          the mapper class, where the new instance shall be bound to
   * @return a new instance of {@link IWrite}
   */
  <T> IQuery<T> createQuery(Class<T> mapper);

  /**
   * Create a new {@link IWrite} bound to the given mapper
   * 
   * @param mapper
   *          the mapper class, where the new instance shall be bound to
   * @return a new instance of {@link IWrite}
   */
  <T> IWrite<T> createWrite(Class<T> mapper);

  /**
   * Create a new {@link IDelete} bound to the given mapper
   * 
   * @param mapper
   *          the mapper class, where the new instance shall be bound to
   * @return a new instance of {@link IWrite}
   */
  <T> IDelete<T> createDelete(Class<T> mapper);

  /**
   * Get or create the {@link IMapperFactory} used by this implementation
   * 
   * @return
   */
  IMapperFactory getMapperFactory();

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
  public IPropertyMapperFactory getPropertyMapperFactory();

  /**
   * Get the {@link IStoreObjectFactory} suitable for the current datastore
   * 
   * @return the instance of {@link IStoreObjectFactory}
   */
  public IStoreObjectFactory getStoreObjectFactory();

  /**
   * Get the instance of {@link IDataStoreSynchronizer} suitable for the current datastore
   * 
   * @return the instance of {@link IDataStoreSynchronizer} for the current datastore or null, if no synchronizer needed
   */
  public IDataStoreSynchronizer getDataStoreSynchronizer();

  /**
   * Get the instance of {@link ITableGenerator} suitable for the given datastore
   * 
   * @return
   */
  public ITableGenerator getTableGenerator();

}
