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
import de.braintags.io.vertx.pojomapper.json.typehandler.JsonTypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.MongoDelete;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.MongoQuery;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.MongoWrite;
import de.braintags.io.vertx.pojomapper.mongo.mapper.MongoMapperFactory;
import de.braintags.io.vertx.pojomapper.mongo.mapper.datastore.MongoTableGenerator;
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
  public MongoDataStore(MongoClient client, JsonObject properties) {
    super(properties);
    this.client = client;
    metaData = new MongoMetaData(client);
    setMapperFactory(new MongoMapperFactory(this));
    setPropertyMapperFactory(new JsonPropertyMapperFactory());
    setTypeHandlerFactory(new JsonTypeHandlerFactory());
    setStoreObjectFactory(new MongoStoreObjectFactory());
    setTableGenerator(new MongoTableGenerator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#createQuery(java.lang.Class)
   */
  @Override
  public <T> IQuery<T> createQuery(Class<T> mapper) {
    return new MongoQuery<T>(mapper, this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#createWrite(java.lang.Class)
   */
  @Override
  public <T> IWrite<T> createWrite(Class<T> mapper) {
    return new MongoWrite<T>(mapper, this);
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
  public MongoClient getMongoClient() {
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

}
