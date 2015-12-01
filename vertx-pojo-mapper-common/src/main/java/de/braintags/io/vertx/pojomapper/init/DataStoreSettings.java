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
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(DataStoreSettings.class);

  /**
   * The property which can be used to set the location of the stored file with Settings information
   */
  public static final String SETTINGS_LOCATION_PROPERTY = "de.braintags.netrelay.settings.path";

  /**
   * The local directory for NetRelay
   */
  public static final String LOCAL_USER_DIRECTORY = System.getProperty("user.home") + "/" + ".netrelay";

  private Class<? extends IDataStoreInit> datastoreInit;
  private Properties properties = new Properties();

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

}
