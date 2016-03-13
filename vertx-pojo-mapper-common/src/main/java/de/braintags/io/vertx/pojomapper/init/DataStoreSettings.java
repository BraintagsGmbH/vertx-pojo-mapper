/*
 * #%L
 * netrelay
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.init;

import java.util.Properties;

import de.braintags.io.vertx.pojomapper.IDataStore;

/**
 * Preferences to initialize an {@link IDataStore}
 * By using this defintion you are able to instantiate an instance of {@link IDataStoreInit} defined by
 * {@link #getDatastoreInit()} and instantiate an {@link IDataStore} by using the Properties inside the current class
 * 
 * @author mremme
 * 
 */
public class DataStoreSettings {
  private Class<? extends IDataStoreInit> datastoreInit;
  private Properties properties = new Properties();
  private String databaseName;

  /**
   * Standard constructor needed for saving as local file
   */
  public DataStoreSettings() {
    // default
  }

  /**
   * Constructor which takes an appropriate instance of {@link IDataStoreInit} and database name
   * 
   * @param datastoreInit
   *          the {@link IDataStoreInit} to be used
   * @param databaseName
   *          the database used
   */
  public DataStoreSettings(Class<? extends IDataStoreInit> datastoreInit, String databaseName) {
    this.datastoreInit = datastoreInit;
    this.databaseName = databaseName;
  }

  /**
   * The implementation of {@link IDataStoreInit} to be used. This implementation will create the
   * concrete implementation of {@link IDataStore}
   * 
   * @return the datastoreInit
   */
  public final Class<? extends IDataStoreInit> getDatastoreInit() {
    return datastoreInit;
  }

  /**
   * The implementation of {@link IDataStoreInit} to be used. This implementation will create the
   * concrete implementation of {@link IDataStore}
   * 
   * @param datastoreInit
   *          the datastoreInit to set
   */
  public final void setDatastoreInit(Class<? extends IDataStoreInit> datastoreInit) {
    this.datastoreInit = datastoreInit;
  }

  /**
   * The properties which will be used by {@link IDataStoreInit} to initialize the {@link IDataStore}. The properties,
   * which are possible to define here, are depending on the concrete implementation of {@link IDataStoreInit}
   * 
   * @return the properties
   */
  public final Properties getProperties() {
    return properties;
  }

  /**
   * The properties which will be used by {@link IDataStoreInit} to initialize the {@link IDataStore}. The properties,
   * which are possible to define here, are depending on the concrete implementation of {@link IDataStoreInit}
   * 
   * @param properties
   *          the properties to set
   */
  public final void setProperties(Properties properties) {
    this.properties = properties;
  }

  /**
   * Defines the name of the database to use
   * 
   * @return the databaseName
   */
  public final String getDatabaseName() {
    return databaseName;
  }

  /**
   * Defines the name of the database to use
   * 
   * @param databaseName
   *          the databaseName to set
   */
  public final void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  @Override
  public String toString() {
    return "database: " + databaseName + "\n " + String.valueOf(properties);
  }
}
