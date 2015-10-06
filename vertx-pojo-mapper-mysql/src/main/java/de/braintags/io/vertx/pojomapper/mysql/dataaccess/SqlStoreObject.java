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

import de.braintags.io.vertx.pojomapper.json.dataaccess.JsonStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * 
 * @author Michael Remme
 * 
 */

public class SqlStoreObject extends JsonStoreObject {

  /**
   * 
   */
  public SqlStoreObject(IMapper mapper, Object entity) {
    super(mapper, entity);
  }

  /**
   * 
   */
  public SqlStoreObject(JsonObject rowResult, IMapper mapper) {
    super(rowResult, mapper);
  }

  /**
   * Generates the sql statement to insert a record into the database
   * 
   * @return the sql statement to be executed
   */
  public SqlSequence generateSqlInsertStatement() {
    throw new UnsupportedOperationException();
  }

  class SqlSequence {
    private String sqlStatement;
    private JsonArray parameters;

    /**
     * @return the sqlStatement
     */
    public final String getSqlStatement() {
      return sqlStatement;
    }

    /**
     * @return the parameters
     */
    public final JsonArray getParameters() {
      return parameters;
    }

  }
}
