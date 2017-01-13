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
package de.braintags.io.vertx.pojomapper.init;

import java.util.List;
import java.util.Properties;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.impl.AbstractDataStore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Abstract implementation of {@link IDataStoreInit}
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractDataStoreInit implements IDataStoreInit {

  /**
   * The property, which defines wether referenced entities inside a mapper shall be handled recursive or not
   */
  public static final String HANDLE_REFERENCED_RECURSIVE_PROP = "handleReferencedRecursive";

  /**
   * The name of the property, which defines wether the MongoClient to be used is shared or not
   */
  public static final String SHARED_PROP = "shared";

  /**
   * The name of the property in the config, which defines the database name
   */
  public static final String DBNAME_PROP = "db_name";

  protected Vertx vertx;
  protected boolean shared = false;
  private JsonObject config;
  protected DataStoreSettings settings;
  protected boolean handleReferencedRecursive = true;

  @Override
  public final void initDataStore(Vertx vertx, DataStoreSettings settings, Handler<AsyncResult<IDataStore>> handler) {
    this.vertx = vertx;
    this.settings = settings;
    internalInit(result -> {
      if (result.failed()) {
        handler.handle(result);
      } else {
        try {
          initEncoder(settings, result.result());
        } catch (Exception e) {
          handler.handle(Future.failedFuture(e));
        }
        handler.handle(result);
      }
    });
  }

  protected void initEncoder(DataStoreSettings settings, IDataStore ds) {
    List<EncoderSettings> esl = settings.getEncoders();
    esl.forEach(es -> ((AbstractDataStore) ds).getEncoderMap().put(es.getName(), es.toEncoder()));
  }

  protected abstract void internalInit(Handler<AsyncResult<IDataStore>> handler);

  protected void checkShared() {
    shared = getBooleanProperty(SHARED_PROP, shared);
  }

  /**
   * Get an instance of JsonConfig, which can be used to init the internal database driver from the settings
   * 
   * @return
   */
  protected final JsonObject getConfig() {
    if (config == null) {
      config = createConfig();
    }
    return config;
  }

  /**
   * Create a new instance of JsonConfig from the settings, which can be used to init the internal database driver
   * 
   * @return
   */
  protected abstract JsonObject createConfig();

  /**
   * Get the name of the database to be used
   * 
   * @return
   */
  protected String getDatabaseName() {
    return settings.getDatabaseName();
  }

  /**
   * Get a property with the given key as boolean
   * 
   * @param name
   *          the key of the property to be fetched
   * @return the value as integer
   */
  protected int getIntegerProperty(String name, int defaultValue) {
    return Integer.parseInt(getProperty(name, String.valueOf(defaultValue)));
  }

  /**
   * Get a property with the given key as boolean
   * 
   * @param name
   *          the key of the property to be fetched
   * @return true or false
   */
  protected boolean getBooleanProperty(String name, boolean defaultValue) {
    return Boolean.parseBoolean(getProperty(name, String.valueOf(defaultValue)));
  }

  /**
   * Get a property with the given key
   * 
   * @param name
   *          the key of the property to be fetched
   * @return a valid value or null
   */
  protected String getProperty(String name, String defaultValue) {
    Properties props = this.settings.getProperties();
    String s = (String) props.get(name);
    if (s != null) {
      s = s.trim();
      if (s.length() > 0) {
        return s;
      }
    }
    return defaultValue;
  }

}
