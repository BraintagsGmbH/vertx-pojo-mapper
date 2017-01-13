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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldCondition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISearchConditionContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.UnknownQueryLogicException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.exception.UnknownQueryOperatorException;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.SortDefinition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.SortDefinition.SortArgument;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlExpression.SqlWhereFragment;
import de.braintags.io.vertx.pojomapper.mysql.mapping.SqlMapper;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlDistanceSearchFunction;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
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

  private String nativeCommand = null;
  private StringBuilder select = new StringBuilder();
  private StringBuilder delete = new StringBuilder();
  private StringBuilder count = new StringBuilder();

  private StringBuilder whereClause = new StringBuilder();
  private StringBuilder orderByClause = new StringBuilder();
  private JsonArray parameters = new JsonArray();

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression#setMapper(de.braintags.io.vertx.
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
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#setNativeCommand(java.lang.Object)
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
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression#handleFinishedSearchCondition(java.
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
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression#handleNullConditionValue(de.
   * braintags.io.vertx.pojomapper.dataaccess.query.IFieldCondition, java.lang.String, io.vertx.core.Handler)
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
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression#buildFieldConditionResult(de.
   * braintags.io.vertx.pojomapper.dataaccess.query.IFieldCondition, java.lang.String, java.lang.Object)
   */
  @Override
  protected SqlWhereFragment buildFieldConditionResult(IFieldCondition fieldCondition, String columnName,
      Object parsedValue) throws UnknownQueryOperatorException {
    QueryOperator operator = fieldCondition.getOperator();
    switch (operator) {
    case CONTAINS:
      parsedValue = "%" + parsedValue + "%";
      break;
    case STARTS:
      parsedValue = parsedValue + "%";
      break;
    case ENDS:
      parsedValue = "%" + parsedValue;
      break;
    default:
      // noop
      break;
    }
    String parsedOperator = translateOperator(operator);

    SqlWhereFragment fragment = new SqlWhereFragment();
    if (parsedValue instanceof SqlDistanceSearchFunction) {
      fragment.whereClause.append(" ").append(((SqlDistanceSearchFunction) parsedValue).getFunctionSequence());
      fragment.parameters.add(((SqlDistanceSearchFunction) parsedValue).getValue());
    } else {
      fragment.whereClause.append(columnName).append(" ");
      fragment.whereClause.append(parsedOperator).append(" ");
      if (parsedValue instanceof Collection<?>) {
        fragment.whereClause.append("(");
        Iterator<?> it = ((Collection<?>) parsedValue).iterator();
        while (it.hasNext()) {
          fragment.whereClause.append("?");
          fragment.parameters.add(it.next());
          if (it.hasNext())
            fragment.whereClause.append(", ");
        }
        fragment.whereClause.append(")");
      } else {
        fragment.whereClause.append("?");
        fragment.parameters.add(parsedValue);
      }
    }
    return fragment;
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
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.AbstractQueryExpression#parseContainerContents(java.util.
   * List, de.braintags.io.vertx.pojomapper.dataaccess.query.ISearchConditionContainer)
   */
  @Override
  protected SqlWhereFragment parseContainerContents(List<SqlWhereFragment> parsedConditionList,
      ISearchConditionContainer container) throws UnknownQueryLogicException {
    String translatedConnector = translateQueryLogic(container.getQueryLogic());

    SqlWhereFragment fragment = new SqlWhereFragment();
    fragment.whereClause.append("(");

    Iterator<SqlWhereFragment> resultIterator = parsedConditionList.iterator();
    while (resultIterator.hasNext()) {
      SqlWhereFragment subFragment = resultIterator.next();
      fragment.whereClause.append(subFragment.whereClause);
      fragment.parameters.addAll(subFragment.parameters);
      if (resultIterator.hasNext())
        fragment.whereClause.append(" ").append(translatedConnector).append(" ");
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
  private String translateQueryLogic(QueryLogic logic) throws UnknownQueryLogicException {
    switch (logic) {
    case AND:
      return "AND";
    case OR:
      return "OR";
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
    private StringBuilder whereClause = new StringBuilder();
    private JsonArray parameters = new JsonArray();
  }
}
