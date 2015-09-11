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

import io.vertx.ext.asyncsql.AsyncSQLClient;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.impl.AbstractDataStore;
import de.braintags.io.vertx.pojomapper.json.mapping.JsonPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.json.typehandler.JsonTypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlStoreObjectFactory;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlWrite;
import de.braintags.io.vertx.pojomapper.mysql.mapping.SqlDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.SqlTableGenerator;

/**
 * 
 * @author Michael Remme
 * 
 */

public class MySqlDataStore extends AbstractDataStore {
  private AsyncSQLClient sqlClient;

  /**
   * 
   */
  public MySqlDataStore(AsyncSQLClient sqlClient, String database) {
    super(database);
    this.sqlClient = sqlClient;
    setMapperFactory(new MapperFactory(this));
    setPropertyMapperFactory(new JsonPropertyMapperFactory());
    setTypeHandlerFactory(new JsonTypeHandlerFactory());
    setStoreObjectFactory(new SqlStoreObjectFactory());
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
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
  }

  /**
   * Get the underlaying instance of {@link AsyncSQLClient} which is used to process requests to the datastore
   * 
   * @return the sqlClient
   */
  public final AsyncSQLClient getSqlClient() {
    return sqlClient;
  }

}
