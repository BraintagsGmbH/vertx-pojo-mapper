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

package de.braintags.vertx.jomnigate.mysql.dataaccess;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;

import de.braintags.vertx.jomnigate.dataaccess.query.IFieldCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchConditionContainer;
import de.braintags.vertx.jomnigate.dataaccess.query.ISortDefinition;
import de.braintags.vertx.jomnigate.dataaccess.query.QueryLogic;
import de.braintags.vertx.jomnigate.dataaccess.query.QueryOperator;
import de.braintags.vertx.jomnigate.dataaccess.query.exception.InvalidQueryValueException;
import de.braintags.vertx.jomnigate.dataaccess.query.exception.UnknownQueryLogicException;
import de.braintags.vertx.jomnigate.dataaccess.query.exception.UnknownQueryOperatorException;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.AbstractQueryExpression;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.GeoSearchArgument;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.SortDefinition;
import de.braintags.vertx.jomnigate.dataaccess.query.impl.SortDefinition.SortArgument;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mysql.dataaccess.SqlExpression.SqlWhereFragment;
import de.braintags.vertx.jomnigate.mysql.mapping.SqlMapper;
import de.braintags.vertx.jomnigate.mysql.typehandler.SqlDistanceSearchFunction;
import de.braintags.vertx.util.json.JsonConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;

/**
 * A helper object, which contains all elements of an sql expression, like the select part, where clause and additional
 * parts
 * 
 * @author Michael Remme
 * 
 */

public class SqlExpression extends AbstractQueryExpression<SqlWhereFragment> {
  private static final String SELECT_STATEMENT = "SELECT %s from %s";
  private static final String DELETE_STATEMENT = "DELETE from %s";
  private static final String COUNT_STATEMENT = "SELECT count(*) from %s";

  private static final Pair<String, QueryOperatorPosition> AND = new ImmutablePair<>("AND",
      QueryOperatorPosition.INFIX);

  private static final Pair<String, QueryOperatorPosition> OR = new ImmutablePair<>("OR",
      QueryOperatorPosition.INFIX);

  private static final Pair<String, QueryOperatorPosition> NOT = new ImmutablePair<>("NOT",
      QueryOperatorPosition.PREFIX);

  private String nativeCommand = null;
  private final StringBuilder select = new StringBuilder();
  private final StringBuilder delete = new StringBuilder();
  private final StringBuilder count = new StringBuilder();

  private final StringBuilder whereClause = new StringBuilder();
  private final StringBuilder orderByClause = new StringBuilder();
  private final JsonArray parameters = new JsonArray();

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.query.impl.AbstractQueryExpression#setMapper(de.braintags.vertx.
   * pojomapper.mapping.IMapper)
   */
  @Override
  public void setMapper(IMapper<?> mapper) {
    super.setMapper(mapper);
    select.append(
        String.format(SELECT_STATEMENT, ((SqlMapper<?>) mapper).getQueryFieldNames(), mapper.getTableInfo().getName()));
    delete.append(String.format(DELETE_STATEMENT, mapper.getTableInfo().getName()));
    count.append(String.format(COUNT_STATEMENT, mapper.getTableInfo().getName()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression#setNativeCommand(java.lang.Object)
   */
  @Override
  public void setNativeCommand(Object nativeCommand) {
    if (nativeCommand instanceof CharSequence) {
      this.nativeCommand = nativeCommand.toString();
    } else {
      throw new UnsupportedOperationException(
          "the sql datastore needs a CharSequence as native format, but is " + nativeCommand.getClass().getName());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.query.impl.AbstractQueryExpression#handleFinishedSearchCondition(java.
   * lang.Object)
   */
  @Override
  protected void handleFinishedSearchCondition(SqlWhereFragment result) {
    whereClause.append(result.whereClause);
    parameters.addAll(result.parameters);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.query.impl.AbstractQueryExpression#handleNullConditionValue(de.braintags.
   * vertx.jomnigate.dataaccess.query.IFieldCondition, java.lang.String, io.vertx.core.Handler)
   */
  @Override
  protected void handleNullConditionValue(IFieldCondition fieldCondition, final String columnName,
      Handler<AsyncResult<SqlWhereFragment>> handler) {
    SqlWhereFragment fragment = new SqlWhereFragment();
    fragment.whereClause.append(columnName).append(" ");
    if (fieldCondition.getOperator() == QueryOperator.EQUALS) {
      fragment.whereClause.append("IS NULL");
    } else if (fieldCondition.getOperator() == QueryOperator.NOT_EQUALS) {
      fragment.whereClause.append("IS NOT NULL");
    } else {
      handler.handle(Future
          .failedFuture(new NullPointerException("Invalid 'null' value for operator " + fieldCondition.getOperator())));
      return;
    }
    handler.handle(Future.succeededFuture(fragment));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.query.impl.AbstractQueryExpression#buildFieldConditionResult(de.braintags.
   * vertx.jomnigate.dataaccess.query.IFieldCondition, java.lang.String, java.lang.Object)
   */
  @Override
  protected SqlWhereFragment buildFieldConditionResult(IFieldCondition fieldCondition, String columnName,
      JsonNode value) throws UnknownQueryOperatorException {
    QueryOperator operator = fieldCondition.getOperator();
    JsonNode parsedValue;
    switch (operator) {
    case CONTAINS:
      parsedValue = new TextNode("%" + value.textValue() + "%");
      break;
    case STARTS:
      parsedValue = new TextNode(value.textValue() + "%");
      break;
    case ENDS:
      parsedValue = new TextNode("%" + value.textValue());
      break;
    default:
      parsedValue = value;
      break;
    }
    String parsedOperator = translateOperator(operator);

    SqlWhereFragment fragment = new SqlWhereFragment();
    if (parsedValue.isObject()) {
      parseGeoSearchArgument(fieldCondition, parsedValue, fragment);
    } else {
      fragment.whereClause.append(columnName).append(" ");
      fragment.whereClause.append(parsedOperator).append(" ");
      if (parsedValue.isArray()) {
        parseArrayValue(parsedValue, fragment);
      } else {
        fragment.whereClause.append("?");
        try {
          fragment.parameters.add(JsonConverter.convertValueNode(parsedValue));
        } catch (IOException e) {
          throw new InvalidQueryValueException(e);
        }
      }
    }
    return fragment;
  }

  private void parseArrayValue(JsonNode parsedValue, SqlWhereFragment fragment) {
    fragment.whereClause.append("(");
    Iterator<JsonNode> it = ((ArrayNode) parsedValue).iterator();
    while (it.hasNext()) {
      fragment.whereClause.append("?");
      try {
        fragment.parameters.add(JsonConverter.convertValueNode(it.next()));
      } catch (IOException e) {
        throw new InvalidQueryValueException(e);
      }
      if (it.hasNext())
        fragment.whereClause.append(", ");
    }
    fragment.whereClause.append(")");
  }

  private void parseGeoSearchArgument(IFieldCondition fieldCondition, JsonNode parsedValue, SqlWhereFragment fragment) {
    GeoSearchArgument geoSearchArgument;
    try {
      geoSearchArgument = Json.mapper.convertValue(parsedValue, GeoSearchArgument.class);
    } catch (Exception e) {
      throw new InvalidQueryValueException(e);
    }
    SqlDistanceSearchFunction sqlDistanceFunction = new SqlDistanceSearchFunction(geoSearchArgument,
        getMapper().getField(fieldCondition.getField().getFieldName()));
    fragment.whereClause.append(" ").append(sqlDistanceFunction.getFunctionSequence());
    fragment.parameters.add(sqlDistanceFunction.getValue());
  }

  /**
   * Parses the query operator into native SQL format
   * 
   * @param operator
   * @return
   * @throws UnknownQueryOperatorException
   *           if the operator is unknown
   */
  private String translateOperator(QueryOperator operator) throws UnknownQueryOperatorException {
    switch (operator) {
    case EQUALS:
      return "=";
    case NOT_EQUALS:
      return "!=";
    case LARGER:
      return ">";
    case LARGER_EQUAL:
      return ">=";
    case SMALLER:
      return "<";
    case SMALLER_EQUAL:
    case NEAR:
      return "<=";
    case IN:
      return "IN";
    case NOT_IN:
      return "NOT IN";
    case STARTS:
    case ENDS:
    case CONTAINS:
      return "LIKE";
    default:
      throw new UnknownQueryOperatorException(operator);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.query.impl.AbstractQueryExpression#parseContainerContents(java.util.
   * List, de.braintags.vertx.jomnigate.dataaccess.query.ISearchConditionContainer)
   */
  @Override
  protected SqlWhereFragment parseContainerContents(List<SqlWhereFragment> parsedConditionList,
      ISearchConditionContainer container) throws UnknownQueryLogicException {
    Pair<String, QueryOperatorPosition> translatedConnector = translateQueryLogic(container.getQueryLogic());

    SqlWhereFragment fragment = new SqlWhereFragment();
    fragment.whereClause.append("(");

    Iterator<SqlWhereFragment> resultIterator = parsedConditionList.iterator();
    while (resultIterator.hasNext()) {
      if (translatedConnector.getValue() == QueryOperatorPosition.PREFIX)
        fragment.whereClause.append(translatedConnector.getKey()).append(" ");
      SqlWhereFragment subFragment = resultIterator.next();
      fragment.whereClause.append(subFragment.whereClause);
      fragment.parameters.addAll(subFragment.parameters);
      if (resultIterator.hasNext()) {
        if (translatedConnector.getValue() == QueryOperatorPosition.INFIX) {
          fragment.whereClause.append(" ").append(translatedConnector.getKey());
        }
        fragment.whereClause.append(" ");
      }
    }
    fragment.whereClause.append(")");
    return fragment;
  }

  /**
   * Translate the logic connector into native SQL format
   * 
   * @param logic
   * @return the native SQL connector value
   * @throws UnknownQueryLogicException
   *           if the logic value is unknown
   */
  private Pair<String, QueryOperatorPosition> translateQueryLogic(QueryLogic logic) throws UnknownQueryLogicException {
    switch (logic) {
    case AND:
        return AND;
    case OR:
        return OR;
    case NOT:
        return NOT;
    default:
      throw new UnknownQueryLogicException(logic);
    }
  }

  /**
   * Checks if the query has parameters
   * 
   * @return true, iff the query has parameters
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
    StringBuilder countExpression = new StringBuilder(count);
    appendWhereClause(countExpression);
    return countExpression.toString();
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
    StringBuilder selectExpression = new StringBuilder(select);
    appendWhereClause(selectExpression);
    appendOrderByClause(selectExpression);
    appendLimitClause(selectExpression);
    return selectExpression.toString();
  }

  /**
   * Get the composite delete statement
   * 
   * @return the complete expression
   */
  public String getDeleteExpression() {
    StringBuilder deleteExpression = new StringBuilder(delete);
    appendWhereClause(deleteExpression);
    return deleteExpression.toString();
  }

  /**
   * Add the limit and offset to the given expression, if they are defined
   * 
   * @param expression
   */
  private void appendLimitClause(StringBuilder expression) {
    if (getLimit() > 0) {
      expression.append(" LIMIT ").append(getLimit());
    }
    if (getOffset() > 0) {
      expression.append(" OFFSET ").append(getOffset());
    }
  }

  /**
   * Append the order by to the expression
   * 
   * @param expression
   */
  private void appendOrderByClause(StringBuilder expression) {
    if (orderByClause.length() > 0) {
      expression.append(" ORDER BY ").append(orderByClause);
    }
  }

  /**
   * Append the where condition to the expression
   * 
   * @param expression
   */
  private void appendWhereClause(StringBuilder expression) {
    if (whereClause.length() > 0) {
      expression.append(" WHERE ").append(whereClause);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.vertx.jomnigate.dataaccess.query.impl.IQueryExpression#addSort(de.braintags.vertx.jomnigate.
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getSelectExpression() + " | " + getDeleteExpression() + " | " + getParameters();
  }

  /**
   * POJO to hold the condition and optionally its parameters of a single fragment of the search condition
   */
  public static class SqlWhereFragment {
    private final StringBuilder whereClause = new StringBuilder();
    private final JsonArray parameters = new JsonArray();
  }
}
