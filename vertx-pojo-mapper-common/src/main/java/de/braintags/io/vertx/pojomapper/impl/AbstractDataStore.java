/*
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

package de.braintags.io.vertx.pojomapper.impl;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableGenerator;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

/**
 * An abstract implementation of {@link IDataStore}
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractDataStore implements IDataStore {
  private IMapperFactory mapperFactory;
  private IPropertyMapperFactory propertyMapperFactory;
  private ITypeHandlerFactory typeHandlerFactory;
  private IStoreObjectFactory storeObjectFactory;
  private ITableGenerator tableGenerator;
  private IDataStoreSynchronizer dataStoreSynchronizer;
  private String database;

  /**
   * 
   */
  public AbstractDataStore(String database) {
    this.database = database;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getMapperFactory()
   */
  @Override
  public final IMapperFactory getMapperFactory() {
    return mapperFactory;
  }

  /**
   * @param mapperFactory
   *          the mapperFactory to set
   */
  protected final void setMapperFactory(IMapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getPropertyMapperFactory()
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

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getTypeHandlerFactory()
   */
  @Override
  public final ITypeHandlerFactory getTypeHandlerFactory() {
    return typeHandlerFactory;
  }

  /**
   * @param typeHandlerFactory
   *          the typeHandlerFactory to set
   */
  protected final void setTypeHandlerFactory(ITypeHandlerFactory typeHandlerFactory) {
    this.typeHandlerFactory = typeHandlerFactory;
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

  /**
   * @return the tableGenerator
   */
  @Override
  public final ITableGenerator getTableGenerator() {
    return tableGenerator;
  }

  /**
   * @param tableGenerator
   *          the tableGenerator to set
   */
  public final void setTableGenerator(ITableGenerator tableGenerator) {
    this.tableGenerator = tableGenerator;
  }

  /**
   * @return the dataStoreSynchronizer
   */
  @Override
  public final IDataStoreSynchronizer getDataStoreSynchronizer() {
    return dataStoreSynchronizer;
  }

  /**
   * @param dataStoreSynchronizer
   *          the dataStoreSynchronizer to set
   */
  public final void setDataStoreSynchronizer(IDataStoreSynchronizer dataStoreSynchronizer) {
    this.dataStoreSynchronizer = dataStoreSynchronizer;
  }

  /**
   * @return the database
   */
  @Override
  public final String getDatabase() {
    return database;
  }

}
