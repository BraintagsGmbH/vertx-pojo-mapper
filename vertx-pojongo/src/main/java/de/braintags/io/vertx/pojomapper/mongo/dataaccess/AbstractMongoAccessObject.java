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

package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.IDataAccessObject;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mongo.MongoDataStore;

/**
 * @author Michael Remme
 */

public abstract class AbstractMongoAccessObject<T> implements IDataAccessObject<T> {
  private Class<T>       mapperClass;
  private MongoDataStore datastore;
  private IMapper        mapper;

  /**
   * 
   */
  public AbstractMongoAccessObject(final Class<T> mapperClass, MongoDataStore datastore) {
    this.mapperClass = mapperClass;
    this.datastore = datastore;
    this.mapper = datastore.getMapperFactory().getMapper(mapperClass);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.IDataAccessObject#getDataStore()
   */
  @Override
  public IDataStore getDataStore() {
    return datastore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.IDataAccessObject#getMapperClass()
   */
  @Override
  public Class<T> getMapperClass() {
    return mapperClass;
  }

  /* (non-Javadoc)
   * @see de.braintags.io.vertx.pojomapper.dataaccess.IDataAccessObject#getMapper()
   */
  @Override
  public IMapper getMapper() {
    return mapper;
  }

}
