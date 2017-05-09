/*
 * #%L
 * jomnigate-sql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.sql.dataaccess;

import de.braintags.vertx.jomnigate.mapping.datastore.IColumnInfo;
import io.vertx.core.json.JsonArray;

class SqlSequence {
  boolean added = false;
  private StringBuilder headStatement;
  private StringBuilder setStatement;
  private StringBuilder whereStatement;
  private Object id;
  private JsonArray parameters = new JsonArray();

  /**
   * Constructor for an insert command
   * 
   * @param tableName
   */
  public SqlSequence(String tableName) {
    headStatement = new StringBuilder("Insert into ").append(tableName);
    setStatement = new StringBuilder(" set ");
  }

  /**
   * Constructor for an update command
   * 
   * @param tableName
   *          the name of the table
   * @param idColInfo
   *          the {@link IColumnInfo} for the id column
   * @param idValue
   *          the id value
   */
  public SqlSequence(String tableName, IColumnInfo idColInfo, Object idValue) {
    headStatement = new StringBuilder("UPDATE ").append(tableName);
    setStatement = new StringBuilder(" set ");
    whereStatement = new StringBuilder(" WHERE ").append(idColInfo.getName()).append(" = ?");
    this.id = idValue;
  }

  void addEntry(String colName, Object value) {
    if (added)
      setStatement.append(", ");
    if (value instanceof SqlFunction) {
      setStatement.append(colName).append(" = ").append(((SqlFunction) value).getFunctionName()).append(" ( ? )");
      parameters.add(((SqlFunction) value).getContent());
    } else {
      setStatement.append(colName).append(" = ?");
      if (value == null) {
        parameters.addNull();
      } else {
        parameters.add(value);
      }
    }
    added = true;
  }

  /**
   * Get the statement
   * 
   * @return the sqlStatement
   */
  public final String getSqlStatement() {
    StringBuilder ret = new StringBuilder(headStatement);
    if (parameters.isEmpty())
      ret.append(" () VALUES ()");// insert into SimpleMapper () VALUES ()
    else
      ret.append(setStatement);
    if (whereStatement != null)
      ret.append(whereStatement);
    return ret.toString();
  }

  /**
   * @return the parameters
   */
  public final JsonArray getParameters() {
    if (id != null)
      return parameters.copy().add(id);
    return parameters;
  }

  @Override
  public String toString() {
    return getSqlStatement() + " | " + getParameters() + " | id: " + id;
  }
}