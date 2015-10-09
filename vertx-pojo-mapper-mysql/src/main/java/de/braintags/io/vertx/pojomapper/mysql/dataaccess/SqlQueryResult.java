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

package de.braintags.io.vertx.pojomapper.mysql.dataaccess;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore;
import de.braintags.io.vertx.util.AbstractCollectionAsync;
import de.braintags.io.vertx.util.IteratorAsync;
import io.vertx.ext.sql.ResultSet;

/**
 * The {@link SqlQueryResult} contains the {@link ResultSet} from a query, by which the entities are created
 * 
 * @author Michael Remme
 * 
 */
public class SqlQueryResult<T> extends AbstractCollectionAsync<T>implements IQueryResult<T> {
  private ResultSet resultSet;
  private MySqlDataStore datastore;
  private SqlQueryRambler query;
  private IMapper mapper;

  /**
   * Creates a lazy loading instance
   * 
   * @param resultSet
   *          The {@link ResultSet} of an executed query
   * @param store
   *          the store which was used to execute the query
   * @param mapper
   *          the underlaying mapper
   * @param query
   *          the {@link SqlQueryRambler}
   */
  public SqlQueryResult(ResultSet resultSet, MySqlDataStore store, IMapper mapper, SqlQueryRambler query) {
    this.resultSet = resultSet;
    this.datastore = store;
    this.query = query;
    this.mapper = mapper;
  }

  @Override
  public int size() {
    return resultSet.getNumRows();
  }

  @Override
  public IteratorAsync<T> iterator() {
    return null;
  }

  @Override
  public IDataStore getDataStore() {
    return datastore;
  }

  @Override
  public IMapper getMapper() {
    return mapper;
  }

  @Override
  public Object getOriginalQuery() {
    return query;
  }

}
