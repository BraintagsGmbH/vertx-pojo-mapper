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
package de.braintags.vertx.jomnigate.init;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.observer.ObserverEventType;
import de.braintags.vertx.util.security.crypt.IEncoder;

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
  private List<EncoderSettings> encoders = new ArrayList<>();
  private boolean clearDatabaseOnInit = false;
  private List<ObserverSettings<?>> observerSettings = new ArrayList<>();

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

  /**
   * The list of EncoderSettings, which are defined by the current application. This list is used to generate the
   * {@link IEncoder}, which shall be usable by the {@link IDataStore#getEncoder(String)}
   * 
   * @return the encoders
   */
  public List<EncoderSettings> getEncoders() {
    return encoders;
  }

  /**
   * The list of EncoderSettings, which are defined by the current application. This list is used to generate the
   * {@link IEncoder}, which shall be usable by the {@link IDataStore#getEncoder(String)}
   * 
   * @param encoders
   *          the encoders to set
   */
  public void setEncoders(List<EncoderSettings> encoders) {
    this.encoders = encoders;
  }

  /**
   * Get if the database should be completely wiped on initialization, deleting everything stored beforehand. Should
   * only be
   * used for unit tests to ensure an empty database for each test.
   * 
   * @return if the database should be cleared on initialization
   */
  public boolean isClearDatabaseOnInit() {
    return clearDatabaseOnInit;
  }

  /**
   * Set if the database should be completely wiped on initialization, deleting everything stored beforehand. Should
   * only be
   * used for unit tests to ensure an empty database for each test.
   * 
   * @param clearDatabaseOnInit
   *          if the database should be cleared on initialization
   */
  public void setClearDatabaseOnInit(boolean clearDatabaseOnInit) {
    this.clearDatabaseOnInit = clearDatabaseOnInit;
  }

  /**
   * Get all defined {@link ObserverSettings}
   * 
   * @return the observerSettings
   */
  public List<ObserverSettings<?>> getObserverSettings() {
    return observerSettings;
  }

  /**
   * Get all {@link ObserverSettings} which are fitting the given mapper class for the event
   * {@link ObserverEventType#BEFORE_MAPPING}
   * 
   * @param mapperClass
   * @return
   */
  public List<ObserverSettings<?>> getObserverSettings(Class<?> mapperClass) {
    List<ObserverSettings<?>> tmpList = new ArrayList<>();
    List<ObserverSettings<?>> osl = getObserverSettings();
    osl.stream().filter(os -> os.isApplicableFor(mapperClass) && os.isApplicableFor(ObserverEventType.BEFORE_MAPPING))
        .forEach(tmpList::add);
    return tmpList;
  }

  /**
   * Get all {@link ObserverSettings} which are fitting the given mapper
   * 
   * @param mapper
   * @return
   */
  public List<ObserverSettings<?>> getObserverSettings(IMapper<?> mapper) {
    List<ObserverSettings<?>> osl = getObserverSettings();
    List<ObserverSettings<?>> tmpList = osl.stream().filter(os -> os.isApplicableFor(mapper))
        .collect(Collectors.toList());
    return tmpList;
  }

  /**
   * @param observerSettings
   *          the observerSettings to set
   */
  private void setObserverSettings(List<ObserverSettings<?>> observerSettings) {
    this.observerSettings = observerSettings;
  }
}
