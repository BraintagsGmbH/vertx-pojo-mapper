/*-
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

package de.braintags.vertx.jomnigate.impl;

import java.util.HashMap;
import java.util.Map;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.KeyGenerator;
import de.braintags.vertx.jomnigate.exception.UnsupportedKeyGenerator;
import de.braintags.vertx.jomnigate.mapping.IDataStoreSynchronizer;
import de.braintags.vertx.jomnigate.mapping.IKeyGenerator;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;
import de.braintags.vertx.jomnigate.mapping.IStoreObjectFactory;
import de.braintags.vertx.jomnigate.mapping.ITriggerContextFactory;
import de.braintags.vertx.jomnigate.mapping.datastore.ITableGenerator;
import de.braintags.vertx.jomnigate.mapping.impl.TriggerContextFactory;
import de.braintags.vertx.jomnigate.mapping.impl.keygen.DebugGenerator;
import de.braintags.vertx.jomnigate.mapping.impl.keygen.DefaultKeyGenerator;
import de.braintags.vertx.util.security.crypt.IEncoder;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * An abstract implementation of {@link IDataStore}
 *
 * @author Michael Remme
 * 
 * @param <S>
 *          the type of the {@link IStoreObjectFactory}
 * @param <U>
 *          the format used by the underlaing {@link IDataStoreSynchronizer}
 *
 */

public abstract class AbstractDataStore<S, U> implements IDataStore<S, U> {
  private Vertx vertx;
  private JsonObject properties;
  private IMapperFactory mapperFactory;
  private IStoreObjectFactory<S> storeObjectFactory;
  private ITableGenerator tableGenerator;
  private IDataStoreSynchronizer<U> dataStoreSynchronizer;
  private Map<String, IKeyGenerator> keyGeneratorMap = new HashMap<>();
  private ITriggerContextFactory triggerContextFactory = new TriggerContextFactory();
  private Map<String, IEncoder> encoderMap = new HashMap<>();
  private int defaultQueryLimit;

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
    defaultQueryLimit = properties.getInteger(DEFAULT_QUERY_LIMIT, 500);
  }

  /**
   * @return the storeObjectFactory
   */
  @Override
  public IStoreObjectFactory<S> getStoreObjectFactory() {
    return storeObjectFactory;
  }

  /**
   * @param storeObjectFactory
   *          the storeObjectFactory to set
   */
  public void setStoreObjectFactory(IStoreObjectFactory<S> storeObjectFactory) {
    this.storeObjectFactory = storeObjectFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getMapperFactory()
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
  public final IDataStoreSynchronizer<U> getDataStoreSynchronizer() {
    return dataStoreSynchronizer;
  }

  /**
   * @param dataStoreSynchronizer
   *          the dataStoreSynchronizer to set
   */
  public final void setDataStoreSynchronizer(IDataStoreSynchronizer<U> dataStoreSynchronizer) {
    this.dataStoreSynchronizer = dataStoreSynchronizer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getProperties()
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

  /**
   * Define all {@link IKeyGenerator}, which are supported by the current instance by using the method
   * {@link #addSupportedKeyGenerator(IKeyGenerator)}
   */
  protected void initSupportedKeyGenerators() {
    addSupportedKeyGenerator(new DebugGenerator(this));
    addSupportedKeyGenerator(new DefaultKeyGenerator(this));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getKeyGenerator(java.lang.String)
   */
  @Override
  public final IKeyGenerator getKeyGenerator(String generatorName) {
    if (generatorName.equals(KeyGenerator.NULL_KEY_GENERATOR)) {
      return null;
    }
    if (keyGeneratorMap.containsKey(generatorName)) {
      return keyGeneratorMap.get(generatorName);
    }
    throw new UnsupportedKeyGenerator("This generator is not supported by the current datastore: " + generatorName);
  }

  @Override
  public Vertx getVertx() {
    return vertx;
  }

  /**
   * @return the triggerContextFactory
   */
  @Override
  public final ITriggerContextFactory getTriggerContextFactory() {
    return triggerContextFactory;
  }

  /**
   * @param triggerContextFactory
   *          the triggerContextFactory to set
   */
  @Override
  public final void setTriggerContextFactory(ITriggerContextFactory triggerContextFactory) {
    this.triggerContextFactory = triggerContextFactory;
  }

  /**
   * The map contains the {@link IEncoder} which are defined for the current instance
   *
   * @return the encoderMap
   */
  public Map<String, IEncoder> getEncoderMap() {
    return encoderMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getEncoder(java.lang.String)
   */
  @Override
  public IEncoder getEncoder(String name) {
    return getEncoderMap().get(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getDefaultQueryLimit()
   */
  @Override
  public int getDefaultQueryLimit() {
    return defaultQueryLimit;
  }

}
