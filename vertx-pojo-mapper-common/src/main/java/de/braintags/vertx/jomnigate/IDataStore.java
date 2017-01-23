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
package de.braintags.vertx.jomnigate;

import de.braintags.vertx.jomnigate.annotation.KeyGenerator;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.exception.UnsupportedKeyGenerator;
import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.mapping.IDataStoreSynchronizer;
import de.braintags.vertx.jomnigate.mapping.IKeyGenerator;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;
import de.braintags.vertx.jomnigate.mapping.IObjectReference;
import de.braintags.vertx.jomnigate.mapping.ITriggerContext;
import de.braintags.vertx.jomnigate.mapping.ITriggerContextFactory;
import de.braintags.vertx.jomnigate.mapping.datastore.ITableGenerator;
import de.braintags.vertx.util.security.crypt.IEncoder;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * IDataStore contains information about the destination datastore and creates the handler objects
 *
 * @author Michael Remme
 *
 */

public interface IDataStore {

  /**
   * The name of the property, which defines the kind, how referenced objects in a mapper are read. In general this
   * defines, wether an {@link IObjectReference} is used or not
   *
   */
  public static final String HANDLE_REFERENCED_RECURSIVE = "handleReferencedRecursive";

  /**
   * The name of the property that defines the limit of queries where no limit is given. It should not be too high to
   * prevent overloading the system when querying large tables without limit.
   */
  public static final String DEFAULT_QUERY_LIMIT = "defaultQueryLimit";

  /**
   * Get the instance of {@link Vertx} where the current instance is belonging to
   *
   * @return the instance of Vertx where inside the current instance was created
   */
  Vertx getVertx();

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
   * Get the instance of {@link IDataStoreSynchronizer} suitable for the current datastore
   *
   * @return the instance of {@link IDataStoreSynchronizer} for the current datastore or null, if no synchronizer needed
   */
  IDataStoreSynchronizer getDataStoreSynchronizer();

  /**
   * Get the instance of {@link ITableGenerator} suitable for the given datastore
   *
   * @return
   */
  ITableGenerator getTableGenerator();

  /**
   * Get the name of the database, the current instance is using
   *
   * @return the name of the schema or database
   */
  String getDatabase();

  /**
   * Get the instance of {@link IDataStoreMetaData}.
   *
   * @return
   */
  IDataStoreMetaData getMetaData();

  /**
   * The properties by which the current instance was initialized
   *
   * @return the properties set for the current instance
   */
  JsonObject getProperties();

  /**
   * Request an {@link IKeyGenerator} with the given name. This method is called by IMapper, when the mapping is
   * processed and an annotation {@link KeyGenerator} was found. The {@link IDataStore} will check for a supported
   * {@link IKeyGenerator} and return it. If there is no {@link IKeyGenerator} supported by this {@link IDataStore}, an
   * {@link UnsupportedKeyGenerator} is thrown
   *
   * @param generatorName
   *          the name of the requested generator
   * @return an instance of {@link IKeyGenerator}
   * @throws UnsupportedKeyGenerator
   *           when the requested generator is not supported by the current instance
   */
  IKeyGenerator getKeyGenerator(String generatorName);

  /**
   * If for an IMapper the annotation {@link KeyGenerator} is undefined, then the default instance is requested here. If
   * the implementation isn't using an {@link IKeyGenerator}, then null will be returned
   *
   * @return the default instance of {@link IKeyGenerator} or null, if the current instance does not need or support
   *         {@link IKeyGenerator}
   */
  IKeyGenerator getDefaultKeyGenerator();

  /**
   * Shutdown the current instance and their used resources
   *
   * @param resultHandler
   */
  void shutdown(Handler<AsyncResult<Void>> resultHandler);

  /**
   * Get the factory, which creates instances of {@link ITriggerContext} when lifecycle methods are called
   *
   * @return
   */
  ITriggerContextFactory getTriggerContextFactory();

  /**
   * Possibility to set the factory to be used from outside, to enabe the usage of customized objects inside a trigger
   *
   * @param factory
   *          the factory to be set
   */
  void setTriggerContextFactory(ITriggerContextFactory factory);

  /**
   * Get the client, which is internally used to connect to the database
   *
   * @return the database specific client
   */
  Object getClient();

  /**
   * Get an instance of {@link IEncoder} with the given name. Encoders to be used can be defined by the
   * {@link DataStoreSettings}
   *
   * @param name
   *          the name of the encoder
   * @return a valid instance of null
   */
  IEncoder getEncoder(String name);

  /**
   * Get the default limit for all queries where no limit is given
   *
   * @return the default limit value
   */
  int getDefaultQueryLimit();

}
