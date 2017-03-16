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

package de.braintags.vertx.jomnigate.mysql;

import de.braintags.vertx.jomnigate.IDataStoreMetaData;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.impl.AbstractDataStore;
import de.braintags.vertx.jomnigate.mapping.IKeyGenerator;
import de.braintags.vertx.jomnigate.mapping.impl.keygen.DefaultKeyGenerator;
import de.braintags.vertx.jomnigate.mysql.dataaccess.SqlDelete;
import de.braintags.vertx.jomnigate.mysql.dataaccess.SqlQuery;
import de.braintags.vertx.jomnigate.mysql.dataaccess.SqlStoreObjectFactory;
import de.braintags.vertx.jomnigate.mysql.dataaccess.SqlWrite;
import de.braintags.vertx.jomnigate.mysql.mapping.SqlDataStoreSynchronizer;
import de.braintags.vertx.jomnigate.mysql.mapping.SqlMapperFactory;
import de.braintags.vertx.jomnigate.mysql.mapping.SqlPropertyMapperFactory;
import de.braintags.vertx.jomnigate.mysql.mapping.datastore.SqlTableGenerator;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlTypeHandlerFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;

/**
 * 
 * @author Michael Remme
 * 
 */
public class MySqlDataStore extends AbstractDataStore<Object, String> {
  /**
   * The name of the property, which describes the database to be used
   */
  public static final String DATABASE_NAME = "database";

  private AsyncSQLClient sqlClient;
  private MySqlMetaData metaData;
  private DefaultKeyGenerator defaultKeyGenerator = new DefaultKeyGenerator(this);

  /**
   * Constructor for a sql based datastore
   * 
   * @param sqlClient
   *          the underlaying sqlclient used to process commands
   * @param database
   *          the name of the database used
   */
  public MySqlDataStore(Vertx vertx, AsyncSQLClient sqlClient, JsonObject properties) {
    super(vertx, properties);
    this.sqlClient = sqlClient;
    metaData = new MySqlMetaData(this);
    setMapperFactory(new SqlMapperFactory(this, new SqlTypeHandlerFactory(), new SqlPropertyMapperFactory()));
    setStoreObjectFactory(new SqlStoreObjectFactory());
    setDataStoreSynchronizer(new SqlDataStoreSynchronizer(this));
    setTableGenerator(new SqlTableGenerator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#createQuery(java.lang.Class)
   */
  @Override
  public <T> IQuery<T> createQuery(Class<T> mapper) {
    return new SqlQuery<>(mapper, this);
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
    return new SqlDelete<>(mapper, this);
  }

  /**
   * @deprecated use getClient() instead
   */
  @Deprecated
  public final AsyncSQLClient getSqlClient() {
    return (AsyncSQLClient) getClient();
  }

  /**
   * Get the underlaying instance of {@link AsyncSQLClient} which is used to process requests to the datastore
   * 
   * @return the sqlClient
   */
  @Override
  public Object getClient() {
    return sqlClient;
  }

  @Override
  public IDataStoreMetaData getMetaData() {
    return metaData;
  }

  /**
   * @return the database
   */
  @Override
  public final String getDatabase() {
    return getProperties().getString(DATABASE_NAME);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#shutdown(io.vertx.core.Handler)
   */
  @Override
  public void shutdown(Handler<AsyncResult<Void>> resultHandler) {
    sqlClient.close(resultHandler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getDefaultKeyGenerator()
   */
  @Override
  public final IKeyGenerator getDefaultKeyGenerator() {
    String genName = getProperties().getString(IKeyGenerator.DEFAULT_KEY_GENERATOR);
    return genName == null ? defaultKeyGenerator : getKeyGenerator(genName);
  }

}
