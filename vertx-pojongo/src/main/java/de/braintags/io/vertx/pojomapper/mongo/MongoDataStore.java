/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mongo;

import io.vertx.ext.mongo.MongoClient;
import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.json.mapping.JsonPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.json.typehandler.JsonTypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory;
import de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.MongoQuery;
import de.braintags.io.vertx.pojomapper.mongo.dataaccess.MongoWrite;
import de.braintags.io.vertx.pojomapper.mongo.mapper.MongoMapperFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

/**
 * An {@link IDataStore} which is dealing with {@link MongoClient}
 * 
 * @author Michael Remme
 * 
 */

public class MongoDataStore implements IDataStore {
  private MongoClient client;
  private MapperFactory mapperFactory = new MongoMapperFactory(this);
  private IPropertyMapperFactory propertyMapperFactory = new JsonPropertyMapperFactory();
  private ITypeHandlerFactory thf = new JsonTypeHandlerFactory();
  public MongoStoreObjectFactory msf = new MongoStoreObjectFactory();

  /**
   * 
   */
  public MongoDataStore(MongoClient client) {
    this.client = client;
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
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getMapperFactory()
   */
  @Override
  public IMapperFactory getMapperFactory() {
    return mapperFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getTypeHandlerFactory()
   */
  @Override
  public ITypeHandlerFactory getTypeHandlerFactory() {
    return thf;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getPropertyMapperFactory()
   */
  @Override
  public IPropertyMapperFactory getPropertyMapperFactory() {
    return propertyMapperFactory;
  }

  /**
   * Get the underlaying instance of {@link MongoClient}
   * 
   * @return the client
   */
  public MongoClient getMongoClient() {
    return client;
  }

  @Override
  public IStoreObjectFactory getStoreObjectFactory() {
    return msf;
  }

}
