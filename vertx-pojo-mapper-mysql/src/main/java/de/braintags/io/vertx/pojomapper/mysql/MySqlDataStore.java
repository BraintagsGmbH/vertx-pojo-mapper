/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mysql;

import de.braintags.io.vertx.pojomapper.IDataStoreMetaData;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.impl.AbstractDataStore;
import de.braintags.io.vertx.pojomapper.json.mapping.JsonPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.impl.keygen.DefaultKeyGenerator;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlDelete;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlQuery;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlStoreObjectFactory;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlWrite;
import de.braintags.io.vertx.pojomapper.mysql.mapping.SqlDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlTableGenerator;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlTypeHandlerFactory;
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
public class MySqlDataStore extends AbstractDataStore {
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
    metaData = new MySqlMetaData(sqlClient);
    setMapperFactory(new MapperFactory(this, new SqlTypeHandlerFactory(), new JsonPropertyMapperFactory(),
        new SqlStoreObjectFactory()));
    setDataStoreSynchronizer(new SqlDataStoreSynchronizer(this));
    setTableGenerator(new SqlTableGenerator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#createQuery(java.lang.Class)
   */
  @Override
  public <T> IQuery<T> createQuery(Class<T> mapper) {
    return new SqlQuery<>(mapper, this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#createWrite(java.lang.Class)
   */
  @Override
  public <T> IWrite<T> createWrite(Class<T> mapper) {
    return new SqlWrite<T>(mapper, this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#createDelete(java.lang.Class)
   */
  @Override
  public <T> IDelete<T> createDelete(Class<T> mapper) {
    return new SqlDelete<T>(mapper, this);
  }

  /**
   * Get the underlaying instance of {@link AsyncSQLClient} which is used to process requests to the datastore
   * 
   * @return the sqlClient
   */
  public final AsyncSQLClient getSqlClient() {
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
   * @see de.braintags.io.vertx.pojomapper.IDataStore#shutdown(io.vertx.core.Handler)
   */
  @Override
  public void shutdown(Handler<AsyncResult<Void>> resultHandler) {
    sqlClient.close(resultHandler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getDefaultKeyGenerator()
   */
  @Override
  public final IKeyGenerator getDefaultKeyGenerator() {
    String genName = getProperties().getString(IKeyGenerator.DEFAULT_KEY_GENERATOR);
    return genName == null ? defaultKeyGenerator : getKeyGenerator(genName);
  }

}
