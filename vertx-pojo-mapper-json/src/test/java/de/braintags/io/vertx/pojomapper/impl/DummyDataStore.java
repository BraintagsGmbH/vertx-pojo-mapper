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

package de.braintags.io.vertx.pojomapper.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.Query;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.json.mapping.JsonPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.json.typehandler.JsonTypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory;
import de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class DummyDataStore implements IDataStore {
  IMapperFactory mf = new MapperFactory(this);
  ITypeHandlerFactory tf = new JsonTypeHandlerFactory();
  IPropertyMapperFactory pmf = new JsonPropertyMapperFactory();

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#createQuery(java.lang.Class)
   */
  @Override
  public <T> IQuery<T> createQuery(Class<T> mapper) {
    return new DummyQuery<T>(mapper, this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#createWrite(java.lang.Class)
   */
  @Override
  public <T> IWrite<T> createWrite(Class<T> mapper) {
    return null;
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
    return mf;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getTypeHandlerFactory()
   */
  @Override
  public ITypeHandlerFactory getTypeHandlerFactory() {
    return tf;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getPropertyMapperFactory()
   */
  @Override
  public IPropertyMapperFactory getPropertyMapperFactory() {
    return pmf;
  }

  class DummyQuery<T> extends Query<T> {

    /**
     * @param mapperClass
     * @param datastore
     */
    public DummyQuery(Class<T> mapperClass, IDataStore datastore) {
      super(mapperClass, datastore);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#execute(io.vertx.core.Handler)
     */
    @Override
    public void execute(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
      throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#executeCount(io.vertx.core.Handler)
     */
    @Override
    public void executeCount(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
      throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#parent()
     */
    @Override
    public Object parent() {
      return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#executeExplain(io.vertx.core.Handler)
     */
    @Override
    public void executeExplain(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
      throw new UnsupportedOperationException();
    }

  }

  @Override
  public IStoreObjectFactory getStoreObjectFactory() {
    throw new UnsupportedOperationException();
  }
}
