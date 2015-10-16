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

import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult;
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
   * 
   */
  public SqlQueryCountResult() {
  }

  /**
   * @param mapper
   * @param dataStore
   * @param count
   * @param originalQuery
   */
  public SqlQueryCountResult(IMapper mapper, IDataStore dataStore, ResultSet resultSet, Object originalQuery) {
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
