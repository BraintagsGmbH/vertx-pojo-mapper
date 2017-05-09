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
import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.sql.dataaccess.SqlStoreObjectFactory;
import de.braintags.vertx.jomnigate.sql.dataaccess.SqlWrite;
import de.braintags.vertx.jomnigate.sql.mapping.SqlDataStoreSynchronizer;
import de.braintags.vertx.jomnigate.sql.mapping.SqlMapperFactory;
import de.braintags.vertx.jomnigate.sql.mapping.SqlTableGenerator;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

/**
 * An implementation of {@link IDataStore} dealing with sql based databases
 * 
 * @author Michael Remme
 * 
 */
public abstract class SqlDataStore extends JsonDatastore<String> {
  /**
   * The name of the property, which describes the database to be used
   */
  public static final String DATABASE_NAME = "database";

  /**
   * @param vertx
   * @param properties
   * @param settings
   */
  public SqlDataStore(Vertx vertx, JsonObject properties, DataStoreSettings settings) {
    super(vertx, properties, settings);
    setMetaData(new SqlMetaData(this));
    setMapperFactory(new SqlMapperFactory(this));
    setTableGenerator(new SqlTableGenerator());
    setStoreObjectFactory(new SqlStoreObjectFactory());
    setDataStoreSynchronizer(new SqlDataStoreSynchronizer(this));
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
    return new SqlWrite<>(mapper, this);
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
    return getProperties().getString(DATABASE_NAME);
  }

}
