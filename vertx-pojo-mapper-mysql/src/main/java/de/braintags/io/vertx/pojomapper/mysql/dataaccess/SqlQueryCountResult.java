/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mysql.dataaccess;

import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.QueryCountResult;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mysql.exception.SqlException;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;

/**
 * An implementation of {@link IQueryCountResult} for sql
 * 
 * @author Michael Remme
 * 
 */

public class SqlQueryCountResult extends QueryCountResult {

  /**
   * Constructor based on various information
   * 
   * @param mapper
   *          the mapper which was used
   * @param dataStore
   *          the datastore which was used
   * @param resultSet
   *          the resultSet which contains the number of records found
   * @param originalQuery
   *          the object which was used to process native the query
   */
  public SqlQueryCountResult(IMapper mapper, IDataStore dataStore, ResultSet resultSet,
      IQueryExpression originalQuery) {
    super(mapper, dataStore, getCount(resultSet), originalQuery);
  }

  private static int getCount(ResultSet set) {
    List<JsonArray> results = set.getResults();
    if (results.isEmpty())
      throw new SqlException("uncorrect result found");
    JsonArray array = results.get(0);
    if (array.isEmpty())
      throw new SqlException("uncorrect result found");
    return array.getInteger(0);
  }
}
