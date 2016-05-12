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

import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.SortDefinition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.SortDefinition.SortArgument;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mysql.mapping.SqlMapper;
import io.vertx.core.json.JsonArray;

/**
 * A helper object, which contains all elements of an sql expression, like the select part, where clause and additional
 * parts
 * 
 * @author Michael Remme
 * 
 */

public class SqlExpression implements IQueryExpression {
  private static final String SELECT_STATEMENT = "SELECT %s from %s";
  private static final String DELETE_STATEMENT = "DELETE from %s";
  private static final String COUNT_STATEMENT = "SELECT count(*) from %s";
  private IMapper mapper;

  private String nativeCommand = null;
  private StringBuilder select = new StringBuilder();
  private StringBuilder delete = new StringBuilder();
  private StringBuilder count = new StringBuilder();
  private int limit;
  private int offset;

  private StringBuilder whereClause = new StringBuilder();
  private StringBuilder orderByClause = new StringBuilder();
  private JsonArray parameters = new JsonArray();
  private Deque<Connector> connectorDeque = new ArrayDeque<Connector>();
  private int openedParenthesis;

  /**
   * Used to render the propriate statements which will be executed in the database later
   * 
   * @param mapper
   *          the underlaying mapper, to retrive the name of the table in the database
   */
  public SqlExpression() {
    connectorDeque.addLast(new Connector("AND"));
  }

  @Override
  public void setMapper(IMapper mapper) {
    this.mapper = mapper;
    select.append(
        String.format(SELECT_STATEMENT, ((SqlMapper) mapper).getQueryFieldNames(), mapper.getTableInfo().getName()));
    delete.append(String.format(DELETE_STATEMENT, mapper.getTableInfo().getName()));
    count.append(String.format(COUNT_STATEMENT, mapper.getTableInfo().getName()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#setNativeCommand(java.lang.Object)
   */
  @Override
  public void setNativeCommand(Object nativeCommand) {
    if (nativeCommand instanceof CharSequence) {
      this.nativeCommand = nativeCommand.toString();
    } else {
      throw new UnsupportedOperationException("the dql datastore needs a CharSequence as native format");
    }
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
   * @param openParenthesis
   *          info, wether a parenthesis shall be opened
   * @return the SqlExpression itself for fluent usage
   */
  @Override
  public SqlExpression startConnectorBlock(String connector, boolean openParenthesis) {
    connectorDeque.addLast(new Connector(connector));
    if (whereClause.length() > 0)
      whereClause.append(" ").append(connector);
    if (openParenthesis) {
      openParenthesis();
    }
    return this;
  }

  /**
   * Append an opening parenthesis and handle the counter for open parenthesis
   */
  @Override
  public IQueryExpression openParenthesis() {
    whereClause.append(" ( ");
    ++openedParenthesis;
    return this;
  }

  /**
   * Append a closing parenthesis and handle the counter for open parenthesis
   */
  @Override
  public IQueryExpression closeParenthesis() {
    whereClause.append(" ) ");
    --openedParenthesis;
    if (openedParenthesis < 0)
      throw new IllegalArgumentException("closed more parenthesis than opened before");
    return this;
  }

  /**
   * Stop the current connector block
   * 
   * @return the SqlExpression itself for fluent usage
   */
  @Override
  public SqlExpression stopConnectorBlock() {
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
  @Override
  public SqlExpression addQuery(String fieldName, String logic, Object value) {
    Connector conn = connectorDeque.getLast();
    if (conn.arguments > 0)
      whereClause.append(" ").append(conn.conn);
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
   * Get the composite statement for a count
   * 
   * @return the complete expression
   */
  public String getCountExpression() {
    if (nativeCommand != null) {
      return nativeCommand;
    }
    StringBuilder complete = new StringBuilder(count);
    appendWhereClause(complete);
    return complete.toString();
  }

  /**
   * Get the composite select statement
   * 
   * @return the complete expression
   */
  public String getSelectExpression() {
    if (nativeCommand != null) {
      return nativeCommand;
    }
    StringBuilder complete = new StringBuilder(select);
    appendWhereClause(complete);
    appendOrderByClause(complete);
    appendLimitClause(complete);
    return complete.toString();
  }

  /**
   * Get the composite delete statement
   * 
   * @return the complete expression
   */
  public String getDeleteExpression() {
    StringBuilder complete = new StringBuilder(delete);
    appendWhereClause(complete);
    return complete.toString();
  }

  private void appendLimitClause(StringBuilder complete) {
    if (whereClause.length() > 0) {
      if (limit > 0) {
        complete.append(" LIMIT ").append(limit);
      }
      if (offset > 0) {
        complete.append(" OFFSET ").append(offset);
      }
    }
  }

  private void appendOrderByClause(StringBuilder complete) {
    if (orderByClause.length() > 0) {
      complete.append(" ORDER BY ").append(orderByClause);
    }
  }

  private void appendWhereClause(StringBuilder complete) {
    if (whereClause.length() > 0) {
      complete.append(" WHERE").append(whereClause);
      int parCount = openedParenthesis;
      while (parCount-- > 0)
        complete.append(" )");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#addSort(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.ISortDefinition)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public IQueryExpression addSort(ISortDefinition<?> sortDef) {
    SortDefinition<?> sd = (SortDefinition<?>) sortDef;
    for (SortArgument sa : sd.getSortArguments()) {
      orderByClause.append(orderByClause.length() == 0 ? "" : ", ").append(sa.fieldName)
          .append(sa.ascending ? " asc" : " desc");
    }
    return this;
  }

  /**
   * Set the limit and the offset ( start ) of a selection
   * 
   * @param limit
   *          the limit of the selection
   * @param offset
   *          the first record
   */
  public void setLimit(int limit, int offset) {
    this.limit = limit;
    this.offset = offset;

  }

  class Connector {
    String conn;
    int arguments;

    Connector(String connector) {
      this.conn = connector;
    }
  }

  @Override
  public String toString() {
    return getSelectExpression() + " | " + getDeleteExpression() + " | " + getParameters();
  }

}
