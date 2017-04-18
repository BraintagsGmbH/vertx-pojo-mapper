/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.mongo;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.IDataStoreMetaData;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.init.DataStoreSettings;
import de.braintags.vertx.jomnigate.json.JsonDatastore;
import de.braintags.vertx.jomnigate.mapping.IKeyGenerator;
import de.braintags.vertx.jomnigate.mongo.dataaccess.MongoDelete;
import de.braintags.vertx.jomnigate.mongo.dataaccess.MongoQuery;
import de.braintags.vertx.jomnigate.mongo.dataaccess.MongoWrite;
import de.braintags.vertx.jomnigate.mongo.init.MongoDataStoreSynchronizer;
import de.braintags.vertx.jomnigate.mongo.mapper.MongoMapperFactory;
import de.braintags.vertx.jomnigate.mongo.mapper.datastore.MongoTableGenerator;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * An {@link IDataStore} which is dealing with {@link MongoClient}
 * 
 * @author Michael Remme
 * 
 */

public class MongoDataStore extends JsonDatastore {

  /**
   * The minimal version of MongoDb, which is expected by the current implementation
   */
  public static final String EXPECTED_VERSION_STARTS_WITH = "3.2.";

  /**
   * The name of the property, which describes the database to be used
   */
  public static final String DATABASE_NAME = "db_name";
  private MongoClient client;
  private MongoMetaData metaData;

  /**
   * Constructor using the given {@link MongoClient}
   * 
   * @param client
   *          the {@link MongoClient} to be used
   * @param database
   *          the name of the database
   */
  public MongoDataStore(Vertx vertx, MongoClient client, JsonObject properties, DataStoreSettings settings) {
    super(vertx, properties, settings);
    this.client = client;
    metaData = new MongoMetaData(this);
    MongoMapperFactory mf = new MongoMapperFactory(this);
    setMapperFactory(mf);
    setStoreObjectFactory(new MongoStoreObjectFactory());
    setDataStoreSynchronizer(new MongoDataStoreSynchronizer(this));
    setTableGenerator(new MongoTableGenerator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#createQuery(java.lang.Class)
   */
  @Override
  public <T> IQuery<T> createQuery(Class<T> mapper) {
    return new MongoQuery<>(mapper, this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#createWrite(java.lang.Class)
   */
  @Override
  public <T> IWrite<T> createWrite(Class<T> mapper) {
    return new MongoWrite<>(mapper, this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#createDelete(java.lang.Class)
   */
  @Override
  public <T> IDelete<T> createDelete(Class<T> mapper) {
    return new MongoDelete<>(mapper, this);
  }

  /**
   * Get the underlaying instance of {@link MongoClient}
   * 
   * @return the client
   */
  @Override
  public Object getClient() {
    return client;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getMetaData()
   */
  @Override
  public IDataStoreMetaData getMetaData() {
    return metaData;
  }

  /**
   * @return the database
   */
  @Override
  public final String getDatabase() {
    return getProperties().getString(DATABASE_NAME).toLowerCase();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#shutdown(io.vertx.core.Handler)
   */
  @Override
  public void shutdown(Handler<AsyncResult<Void>> resultHandler) {
    try {
      client.close();
      resultHandler.handle(Future.succeededFuture());
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(new RuntimeException(e)));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getDefaultKeyGenerator()
   */
  @Override
  public final IKeyGenerator getDefaultKeyGenerator() {
    String genName = getProperties().getString(IKeyGenerator.DEFAULT_KEY_GENERATOR);
    return genName == null ? null : getKeyGenerator(genName);
  }

}
