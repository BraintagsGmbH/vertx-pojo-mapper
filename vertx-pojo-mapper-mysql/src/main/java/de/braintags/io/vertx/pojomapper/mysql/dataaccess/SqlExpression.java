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

import java.util.ArrayDeque;
import java.util.Deque;

import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import io.vertx.core.json.JsonArray;

/**
 * A helper object, which contains all elements of an sql expression, like the select part, where clause and additional
 * parts
 * 
 * @author Michael Remme
 * 
 */

public class SqlExpression {
  private static final String SELECT_STATEMENT = "SELECT * from %s";
  private static final String DELETE_STATEMENT = "DELETE from %s";

  private StringBuilder select = new StringBuilder();
  private StringBuilder delete = new StringBuilder();
  private StringBuilder whereClause = new StringBuilder();
  private JsonArray parameters = new JsonArray();
  private Deque<Connector> connectorDeque = new ArrayDeque<Connector>();

  /**
   * 
   */
  public SqlExpression(IMapper mapper) {
    select.append(String.format(SELECT_STATEMENT, mapper.getTableInfo().getName()));
    delete.append(String.format(DELETE_STATEMENT, mapper.getTableInfo().getName()));
    connectorDeque.addLast(new Connector("AND"));
  }

  /**
   * Add a text part into the where clause
   * 
   * @param text
   *          the text to be added
   * @return the SqlExpression itself for fluent usage
   */
  public SqlExpression addWhere(String text) {
    whereClause.append(text);
    return this;
  }

  /**
   * Start an AND / OR block
   * 
   * @param connector
   *          the connector AND / OR
   * @return the SqlExpression itself for fluent usage
   */
  public SqlExpression startConnectorBlock(String connector) {
    connectorDeque.addLast(new Connector(connector));
    if (whereClause.length() > 0)
      whereClause.append(" ").append(connector);
    whereClause.append(" ( ");
    return this;
  }

  /**
   * Stop the current connector block
   * 
   * @return the SqlExpression itself for fluent usage
   */
  public SqlExpression stopConnectorBlock() {
    whereClause.append(" ) ");
    connectorDeque.removeLast();
    return this;
  }

  /**
   * add a query expression
   * 
   * @param fieldName
   *          the name to search in
   * @param logic
   *          the logic
   * @param value
   *          the value
   * @return the SqlExpression itself for fluent usage
   */
  public SqlExpression addQuery(String fieldName, String logic, Object value) {
    Connector conn = connectorDeque.getLast();
    if (conn.arguments > 0)
      whereClause.append(" ").append(conn.connector);
    if (logic.equalsIgnoreCase("IN") || logic.equalsIgnoreCase("NOT IN")) {
      addQueryIn(fieldName, logic, (JsonArray) value);
    } else {
      whereClause.append(" ").append(fieldName).append(" ").append(logic).append(" ?");
      parameters.add(value);
    }
    conn.arguments++;
    return this;
  }

  private void addQueryIn(String fieldName, String logic, JsonArray values) {
    whereClause.append(" ").append(fieldName).append(" ").append(logic).append(" ( ");
    for (int i = 0; i < values.size(); i++) {
      whereClause.append(i == 0 ? "?" : ", ?");
      parameters.add(values.getValue(i));
    }

    whereClause.append(" ) ");
  }

  /**
   * Get the information, whether the query has parameters
   * 
   * @return true, if query has parameters
   */
  public boolean hasQueryParameters() {
    return parameters != null && !parameters.isEmpty();
  }

  /**
   * Get the parameters of the expression
   * 
   * @return
   */
  public JsonArray getParameters() {
    return parameters;
  }

  /**
   * Get the composite select statement
   * 
   * @return the complete expression
   */
  public String getSelectExpression() {
    StringBuilder complete = new StringBuilder(select);
    if (whereClause.length() > 0)
      complete.append(" WHERE").append(whereClause);
    return complete.toString();
  }

  /**
   * Get the composite delete statement
   * 
   * @return the complete expression
   */
  public String getDeleteExpression() {
    StringBuilder complete = new StringBuilder(delete);
    if (whereClause.length() > 0)
      complete.append(" WHERE").append(whereClause);
    return complete.toString();
  }

  class Connector {
    String connector;
    int arguments;

    Connector(String connector) {
      this.connector = connector;
    }
  }

  @Override
  public String toString() {
    return getSelectExpression() + " | " + getDeleteExpression() + " | " + getParameters();
  }
}
