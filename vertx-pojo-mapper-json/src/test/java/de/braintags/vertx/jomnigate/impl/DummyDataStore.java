/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.impl;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.IDataStoreMetaData;
import de.braintags.vertx.jomnigate.dataaccess.delete.IDelete;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryCountResult;
import de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.Query;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.json.mapping.JsonPropertyMapperFactory;
import de.braintags.vertx.jomnigate.json.typehandler.JsonTypeHandlerFactory;
import de.braintags.vertx.jomnigate.mapping.IDataStoreSynchronizer;
import de.braintags.vertx.jomnigate.mapping.IKeyGenerator;
import de.braintags.vertx.jomnigate.mapping.IMapperFactory;
import de.braintags.vertx.jomnigate.mapping.ITriggerContextFactory;
import de.braintags.vertx.jomnigate.mapping.datastore.ITableGenerator;
import de.braintags.vertx.jomnigate.mapping.impl.MapperFactory;
import de.braintags.vertx.util.security.crypt.IEncoder;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 *
 *
 * @author Michael Remme
 *
 */

public class DummyDataStore implements IDataStore {
  IMapperFactory mf = new MapperFactory(this, new JsonTypeHandlerFactory(), new JsonPropertyMapperFactory(), null);
  ITableGenerator tg = new DummyTableGenerator();
  String database;
  private JsonObject properties;
  private ITriggerContextFactory triggerContextFactory;

  public DummyDataStore() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#createQuery(java.lang.Class)
   */
  @Override
  public <T> IQuery<T> createQuery(Class<T> mapper) {
    return new DummyQuery<>(mapper, this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#createWrite(java.lang.Class)
   */
  @Override
  public <T> IWrite<T> createWrite(Class<T> mapper) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#createDelete(java.lang.Class)
   */
  @Override
  public <T> IDelete<T> createDelete(Class<T> mapper) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getMapperFactory()
   */
  @Override
  public IMapperFactory getMapperFactory() {
    return mf;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void setUnsupported(Handler handler) {
    handler.handle(Future.failedFuture(new UnsupportedOperationException()));
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
     * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#executeExplain(io.vertx.core.Handler)
     */
    @Override
    public void executeExplain(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
      setUnsupported(resultHandler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.braintags.vertx.jomnigate.dataaccess.query.IQuery#addNativeCommand(java.lang.Object)
     */
    @Override
    public void setNativeCommand(Object command) {
      throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.braintags.vertx.jomnigate.dataaccess.query.impl.Query#internalExecute(de.braintags.vertx.jomnigate.
     * dataaccess.query.impl.IQueryExpression, io.vertx.core.Handler)
     */
    @Override
    protected void internalExecute(IQueryExpression queryExpression,
        Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
      setUnsupported(resultHandler);

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.braintags.vertx.jomnigate.dataaccess.query.impl.Query#internalExecuteCount(de.braintags.vertx.
     * pojomapper.dataaccess.query.impl.IQueryExpression, io.vertx.core.Handler)
     */
    @Override
    protected void internalExecuteCount(IQueryExpression queryExpression,
        Handler<AsyncResult<IQueryCountResult>> resultHandler) {
      setUnsupported(resultHandler);

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.braintags.vertx.jomnigate.dataaccess.query.impl.Query#getQueryExpressionClass()
     */
    @Override
    protected Class<? extends IQueryExpression> getQueryExpressionClass() {
      throw new UnsupportedOperationException();
    }

  }

  @Override
  public IDataStoreSynchronizer getDataStoreSynchronizer() {
    return null;
  }

  @Override
  public ITableGenerator getTableGenerator() {
    return tg;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getDatabase()
   */
  @Override
  public String getDatabase() {
    return database;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getMetaData()
   */
  @Override
  public IDataStoreMetaData getMetaData() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getProperties()
   */
  @Override
  public JsonObject getProperties() {
    return properties;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getKeyGenerator(java.lang.String)
   */
  @Override
  public IKeyGenerator getKeyGenerator(String generatorName) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getDefaultKeyGenerator()
   */
  @Override
  public IKeyGenerator getDefaultKeyGenerator() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getVertx()
   */
  @Override
  public Vertx getVertx() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#shutdown(io.vertx.core.Handler)
   */
  @Override
  public void shutdown(Handler<AsyncResult<Void>> resultHandler) {
  }

  @Override
  public final ITriggerContextFactory getTriggerContextFactory() {
    return triggerContextFactory;
  }

  /**
   * @param triggerContextFactory
   *          the triggerContextFactory to set
   */
  @Override
  public final void setTriggerContextFactory(ITriggerContextFactory triggerContextFactory) {
    this.triggerContextFactory = triggerContextFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getClient()
   */
  @Override
  public Object getClient() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getEncoder(java.lang.String)
   */
  @Override
  public IEncoder getEncoder(String name) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.IDataStore#getDefaultQueryLimit()
   */
  @Override
  public int getDefaultQueryLimit() {
    return 500;
  }

}
