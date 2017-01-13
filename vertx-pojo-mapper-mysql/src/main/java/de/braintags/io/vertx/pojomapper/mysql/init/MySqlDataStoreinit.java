/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mysql.init;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.init.AbstractDataStoreInit;
import de.braintags.io.vertx.pojomapper.init.DataStoreSettings;
import de.braintags.io.vertx.pojomapper.init.EncoderSettings;
import de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.impl.keygen.DefaultKeyGenerator;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore;
import de.braintags.io.vertx.util.exception.ParameterRequiredException;
import de.braintags.io.vertx.util.security.crypt.impl.StandardEncoder;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MySqlDataStoreinit extends AbstractDataStoreInit {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(MySqlDataStoreinit.class);
  private static boolean handleReferencedRecursive = true;

  /**
   * The property which defines the host of the database
   */
  public static final String HOST_PROPERTY = "host";

  /**
   * The property which defines the username to be used to access the database
   */
  public static final String USERNAME_PROPERTY = "username";

  /**
   * The property which defines the password to be used to access the database
   */
  public static final String PASSWORD_PROPERTY = "password";

  /**
   * The port to be used to connect to the database
   */
  public static final String PORT_PROPERTY = "port";

  public static final int DEFAULT_PORT = 3306;

  private static final String DEFAULT_KEY_GENERATOR = DefaultKeyGenerator.NAME;
  private MySqlDataStore datastore;
  private AsyncSQLClient mySQLClient;

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.init.AbstractDataStoreInit#internalInit(io.vertx.core.Handler)
   */
  @Override
  protected void internalInit(Handler<AsyncResult<IDataStore>> handler) {
    try {
      mySQLClient = shared ? MySQLClient.createShared(vertx, getConfig())
          : MySQLClient.createNonShared(vertx, getConfig());
      datastore = new MySqlDataStore(vertx, mySQLClient, getConfig());
      handler.handle(Future.succeededFuture(datastore));
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  /**
   * Returns true, if there are existing system properties, by which {@link DataStoreSettings} for MySql can be created
   * 
   * @return true, if available
   */
  public static boolean hasSystemProperties() {
    return System.getProperty("MySqlDataStoreContainer.username", null) != null;
  }

  /**
   * This method creates new datastore settings for MySql by using system properties:
   * <UL>
   * <LI>MySqlDataStoreContainer.username to set the username of the database
   * <LI>MySqlDataStoreContainer.password to set the password of the database
   * <LI>MySqlDataStoreContainer.host to set the host of the database
   * <LI>defaultKeyGenerator to set the name of the default keygenerator to be used
   * </UL>
   * 
   * @return
   */
  public static DataStoreSettings createSettings() {
    String database = "test";
    String username = System.getProperty("MySqlDataStoreContainer.username", null);
    if (username == null) {
      throw new ParameterRequiredException("you must set the property 'MySqlDataStoreContainer.username'");
    }
    String password = System.getProperty("MySqlDataStoreContainer.password", null);
    if (password == null) {
      throw new ParameterRequiredException("you must set the property 'MySqlDataStoreContainer.password'");
    }
    String host = System.getProperty("MySqlDataStoreContainer.host", null);
    if (host == null) {
      throw new ParameterRequiredException("you must set the property 'MySqlDataStoreContainer.host'");
    }
    String keyGenerator = System.getProperty(IKeyGenerator.DEFAULT_KEY_GENERATOR, DEFAULT_KEY_GENERATOR);
    DataStoreSettings settings = createDefaultSettings();
    settings.setDatabaseName(database);
    settings.getProperties().put(MySqlDataStoreinit.HOST_PROPERTY, host);
    settings.getProperties().put(MySqlDataStoreinit.PORT_PROPERTY, MySqlDataStoreinit.DEFAULT_PORT);
    settings.getProperties().put(MySqlDataStoreinit.USERNAME_PROPERTY, username);
    settings.getProperties().put(MySqlDataStoreinit.PASSWORD_PROPERTY, password);
    settings.getProperties().put(MySqlDataStoreinit.SHARED_PROP, "true");
    settings.getProperties().put(MySqlDataStoreinit.HANDLE_REFERENCED_RECURSIVE_PROP, handleReferencedRecursive);
    settings.getProperties().put(IKeyGenerator.DEFAULT_KEY_GENERATOR, keyGenerator);
    LOGGER.info("SETTINGS ARE: " + settings.toString());
    return settings;
  }

  /**
   * Helper method which creates the default settings for an instance of {@link MySqlDataStore}
   * 
   * @return default instance of {@link DataStoreSettings} to init a {@link MySqlDataStore}
   */
  public static final DataStoreSettings createDefaultSettings() {
    DataStoreSettings settings = new DataStoreSettings(MySqlDataStoreinit.class, "testdatabase");
    settings.getProperties().put(HOST_PROPERTY, "localhost");
    settings.getProperties().put(PORT_PROPERTY, DEFAULT_PORT);
    settings.getProperties().put(USERNAME_PROPERTY, "testusername");
    settings.getProperties().put(PASSWORD_PROPERTY, "testpassword");
    settings.getProperties().put(SHARED_PROP, "false");
    settings.getProperties().put(HANDLE_REFERENCED_RECURSIVE_PROP, "true");
    settings.getProperties().put(IKeyGenerator.DEFAULT_KEY_GENERATOR, DEFAULT_KEY_GENERATOR);

    EncoderSettings es = new EncoderSettings();
    es.setName(StandardEncoder.class.getSimpleName());
    es.setEncoderClass(StandardEncoder.class);
    es.getProperties().put(StandardEncoder.SALT_PROPERTY, StandardEncoder.generateSalt());
    settings.getEncoders().add(es);

    return settings;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.init.AbstractDataStoreInit#createConfig()
   */
  @Override
  protected JsonObject createConfig() {
    return new JsonObject().put(HOST_PROPERTY, getHostName()).put(USERNAME_PROPERTY, getUserName())
        .put(IDataStore.HANDLE_REFERENCED_RECURSIVE, handleReferencedRecursive).put(PASSWORD_PROPERTY, getPassword())
        .put("database", getDatabaseName()).put(PORT_PROPERTY, getPort(DEFAULT_PORT))
        .put(IKeyGenerator.DEFAULT_KEY_GENERATOR, getKeygeneratorName());
  }

  private int getPort(int defaultPort) {
    Object p = settings.getProperties().get(PORT_PROPERTY);
    if (p == null) {
      return defaultPort;
    } else if (p instanceof String) {
      return Integer.parseInt((String) p);
    } else {
      return (Integer) p;
    }
  }

  private String getKeygeneratorName() {
    return getProperty(IKeyGenerator.DEFAULT_KEY_GENERATOR, null);
  }

  private String getHostName() {
    return getProperty(HOST_PROPERTY, null);
  }

  private String getUserName() {
    return getProperty(USERNAME_PROPERTY, null);
  }

  private String getPassword() {
    return getProperty(PASSWORD_PROPERTY, null);
  }

  /**
   * Get the instance of {@link AsyncSQLClient}. Method must be called after the method
   * {@link #initDataStore(io.vertx.core.Vertx, de.braintags.io.vertx.pojomapper.init.DataStoreSettings, Handler)}
   * 
   * @return the mySQLClient the initialized instance or null
   */
  public final AsyncSQLClient getMySQLClient() {
    return mySQLClient;
  }

}
