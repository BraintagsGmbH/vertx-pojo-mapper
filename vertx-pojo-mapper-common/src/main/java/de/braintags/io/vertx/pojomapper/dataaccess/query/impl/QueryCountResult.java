/*
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

package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;

/**
 * The default implementation of {@link IQueryCountResult}
 * 
 * @author Michael Remme
 * 
 */

public class QueryCountResult implements IQueryCountResult {
  private IMapper mapper;
  private IDataStore dataStore;
  private long count;
  private Object originalQuery;

  /**
   * Constructor based on various information
   * 
   * @param mapper
   *          the mapper which was used
   * @param dataStore
   *          the datastore which was used
   * @param count
   *          the number of instances found
   * @param originalQuery
   *          the object which was used to process native the query
   */
  public QueryCountResult(IMapper mapper, IDataStore dataStore, long count, Object originalQuery) {
    this.mapper = mapper;
    this.dataStore = dataStore;
    this.count = count;
    this.originalQuery = originalQuery;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult#getDataStore()
   */
  @Override
  public IDataStore getDataStore() {
    return dataStore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult#getMapper()
   */
  @Override
  public IMapper getMapper() {
    return mapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult#getOriginalQuery()
   */
  @Override
  public Object getOriginalQuery() {
    return originalQuery;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult#getCount()
   */
  @Override
  public long getCount() {
    return count;
  }

}
