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
package de.braintags.io.vertx.pojomapper.mongo;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.IDataStoreMetaData;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.impl.AbstractDataStore;
import de.braintags.io.vertx.pojomapper.json.mapping.JsonPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.impl.keygen.DefaultKeyGenerator;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.MongoDelete;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.MongoQuery;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.MongoWrite;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.QueryLogicTranslator;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.QueryOperatorTranslator;
import de.braintags.io.vertx.pojomapper.mongo.init.MongoDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mongo.mapper.MongoMapperFactory;
import de.braintags.io.vertx.pojomapper.mongo.mapper.datastore.MongoTableGenerator;
import de.braintags.io.vertx.pojomapper.mongo.typehandler.MongoTypeHandlerFactory;
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

public class MongoDataStore extends AbstractDataStore implements IDataStore {

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
  public MongoDataStore(Vertx vertx, MongoClient client, JsonObject properties) {
    super(vertx, properties, new QueryLogicTranslator(), new QueryOperatorTranslator());
    this.client = client;
    metaData = new MongoMetaData(this);
    MongoMapperFactory mf = new MongoMapperFactory(this, new MongoTypeHandlerFactory(), new JsonPropertyMapperFactory(),
        new MongoStoreObjectFactory());
    setMapperFactory(mf);
    setDataStoreSynchronizer(new MongoDataStoreSynchronizer(this));
    setTableGenerator(new MongoTableGenerator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#createQuery(java.lang.Class)
   */
  @Override
  public <T> IQuery<T> createQuery(Class<T> mapper) {
    return new MongoQuery<>(mapper, this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#createWrite(java.lang.Class)
   */
  @Override
  public <T> IWrite<T> createWrite(Class<T> mapper) {
    return new MongoWrite<>(mapper, this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#createDelete(java.lang.Class)
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
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getMetaData()
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
    return getProperties().getString(DATABASE_NAME);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#shutdown(io.vertx.core.Handler)
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
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getDefaultKeyGenerator()
   */
  @Override
  public final IKeyGenerator getDefaultKeyGenerator() {
    String genName = getProperties().getString(IKeyGenerator.DEFAULT_KEY_GENERATOR, DefaultKeyGenerator.NAME);
    return genName == null ? null : getKeyGenerator(genName);
  }

}
