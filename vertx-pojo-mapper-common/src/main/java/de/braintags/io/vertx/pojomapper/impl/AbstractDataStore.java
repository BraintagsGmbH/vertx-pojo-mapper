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

import java.util.HashMap;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.exception.UnsupportedKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableGenerator;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * An abstract implementation of {@link IDataStore}
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractDataStore implements IDataStore {
  private Vertx vertx;
  private JsonObject properties;
  private IMapperFactory mapperFactory;
  private IPropertyMapperFactory propertyMapperFactory;
  private IStoreObjectFactory storeObjectFactory;
  private ITableGenerator tableGenerator;
  private IDataStoreSynchronizer dataStoreSynchronizer;
  private Map<String, IKeyGenerator> keyGeneratorMap = new HashMap<>();

  /**
   * Create a new instance. The possible properties are defined by its concete implementation
   * 
   * @param vertx
   *          the instance if {@link Vertx} used
   * @param properties
   *          the properties by which the new instance is created
   */
  public AbstractDataStore(Vertx vertx, JsonObject properties) {
    this.vertx = vertx;
    this.properties = properties;
    initSupportedKeyGenerators();
  }

  /**
   * Define all {@link IKeyGenerator}, which are supported by the current instance by using the method
   * {@link #addSupportedKeyGenerator(IKeyGenerator)}
   */
  protected abstract void initSupportedKeyGenerators();

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

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getProperties()
   */
  @Override
  public JsonObject getProperties() {
    return properties;
  }

  /**
   * Add an {@link IKeyGenerator} supported by the current instance
   * 
   * @param generator
   */
  protected void addSupportedKeyGenerator(IKeyGenerator generator) {
    keyGeneratorMap.put(generator.getName(), generator);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getKeyGenerator(java.lang.String)
   */
  @Override
  public final IKeyGenerator getKeyGenerator(String generatorName) {
    if (keyGeneratorMap.containsKey(generatorName)) {
      return keyGeneratorMap.get(generatorName);
    }
    throw new UnsupportedKeyGenerator("This generator is not supported by the current datastore: " + generatorName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getDefaultKeyGenerator()
   */
  @Override
  public IKeyGenerator getDefaultKeyGenerator() {
    String genName = getProperties().getString(IKeyGenerator.DEFAULT_KEY_GENERATOR);
    return genName == null ? null : getKeyGenerator(genName);
  }

  @Override
  public Vertx getVertx() {
    return vertx;
  }

}
