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

import java.util.Set;

import de.braintags.io.vertx.pojomapper.json.dataaccess.JsonStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;
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
   * Generates the sql statement to insert a record into the database and a list of fitting parameters
   * 
   * @return the sql statement to be executed
   */
  public SqlSequence generateSqlInsertStatement() {
    ITableInfo tInfo = getMapper().getTableInfo();
    SqlSequence sequence = new SqlSequence(tInfo.getName());

    Set<String> fieldNames = getMapper().getFieldNames();
    for (String fieldName : fieldNames) {
      IField field = getMapper().getField(fieldName);
      if (field != getMapper().getIdField()) {
        sequence.addEntry(tInfo.getColumnInfo(field).getName(), get(field));
      }
    }
    return sequence;
  }

  class SqlSequence {
    boolean added = false;
    private StringBuilder sqlStatement;
    private JsonArray parameters = new JsonArray();

    public SqlSequence(String tableName) {
      sqlStatement = new StringBuilder("Insert into ").append(tableName).append(" set ");
    }

    void addEntry(String colName, Object value) {
      if (value == null)
        return;
      if (added)
        sqlStatement.append(", ");
      sqlStatement.append(colName).append(" = ?");
      parameters.add(value);
      added = true;

    }

    /**
     * @return the sqlStatement
     */
    public final String getSqlStatement() {
      return sqlStatement.toString();
    }

    /**
     * @return the parameters
     */
    public final JsonArray getParameters() {
      return parameters;
    }

  }
}
