/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.dataaccess.impl;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.IDataAccessObject;
import de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.ISyncResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Abstract implementation of {@link IDataAccessObject}
 * 
 * @author Michael Remme
 * @param <T>
 *          the mapper class which is dealed by the current instance
 */

public abstract class AbstractDataAccessObject<T> implements IDataAccessObject<T> {

  private Class<T> mapperClass;
  private IDataStore datastore;
  private IMapper mapper;

  /**
   * Creates an instance for the given mapper class and requests an {@link IMapper} definition from
   * the {@link IDataStore}
   * 
   * @param mapperClass
   *          the class to deal with
   * @param datastore
   *          the datastore to be used for all actions of the current instance
   */
  public AbstractDataAccessObject(final Class<T> mapperClass, IDataStore datastore) {
    this.mapperClass = mapperClass;
    this.datastore = datastore;
    this.mapper = datastore.getMapperFactory().getMapper(mapperClass);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.pojomapper.dataaccess.IDataAccessObject#getDataStore()
   */
  @Override
  public IDataStore getDataStore() {
    return datastore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.pojomapper.dataaccess.IDataAccessObject#getMapperClass()
   */
  @Override
  public Class<T> getMapperClass() {
    return mapperClass;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.pojomapper.dataaccess.IDataAccessObject#getMapper()
   */
  @Override
  public IMapper getMapper() {
    return mapper;
  }

  /**
   * If a Synchronizer is defined, then it is executed
   * 
   * @param resultHandler
   *          resultHandler receives the ISyncResult or null, if no synchronizer is defined
   */
  protected void sync(Handler<AsyncResult<ISyncResult>> resultHandler) {
    IDataStoreSynchronizer syncer = getDataStore().getDataStoreSynchronizer();
    if (syncer != null) {
      syncer.synchronize(getMapper(), resultHandler);
    } else {
      // synchronization was done already for the current intstance
      resultHandler.handle(Future.succeededFuture());
    }
  }
}
