/*
 * #%L
 * jomnigate-sql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.sql;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.IDataStoreMetaData;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.mapping.IKeyGenerator;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

/**
 * An implementation of {@link IDataStore} dealing with sql based databases
 * 
 * @author Michael Remme
 * 
 */
public abstract class SqlDataStore extends JsonDatastore {

  /**
   * @param vertx
   * @param properties
   * @param settings
   */
  public SqlDataStore(Vertx vertx, JsonObject properties, DataStoreSettings settings) {
    super(vertx, properties, settings);
  }

  /**
   * Retrive a connection to the datastore
   * 
   * @return future, which contains the connection
   */
  public abstract Future<SQLConnection> getConnection();

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#createQuery(java.lang.Class)
   */
  @Override
  public <T> IQuery<T> createQuery(Class<T> mapper) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#createWrite(java.lang.Class)
   */
  @Override
  public <T> IWrite<T> createWrite(Class<T> mapper) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#createDelete(java.lang.Class)
   */
  @Override
  public <T> IDelete<T> createDelete(Class<T> mapper) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getDatabase()
   */
  @Override
  public String getDatabase() {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getMetaData()
   */
  @Override
  public IDataStoreMetaData getMetaData() {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getDefaultKeyGenerator()
   */
  @Override
  public IKeyGenerator getDefaultKeyGenerator() {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#shutdown(io.vertx.core.Handler)
   */
  @Override
  public void shutdown(Handler<AsyncResult<Void>> resultHandler) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getClient()
   */
  @Override
  public Object getClient() {
    throw new UnsupportedOperationException();
  }

}
